import argparse
from datetime import datetime, timedelta
import logging
import os
import sys
import traceback
from pathlib import Path
from logging.handlers import RotatingFileHandler

import json
from types import SimpleNamespace

import growattServer
import ShellyPy
import schedule
import time
import functools

import paho.mqtt.client as mqtt

from threading import Condition

from aiohttp import ClientSession
import asyncio
import aioaseko

# let's define the Growatt API on a global level for easier access
growatt_api = None

# let's also define the Shelly API on a global level for easier access
shelly = None

# and finally, define the MQTT Client on a global level for easier access
mqtt_client = mqtt.Client()

filtration_started_at = None

default_config = """
{
    "battery_levels": {
        "almost_charged": 95.0,
        "rapidly_charging": 85.0,
        "rapidly_discharging": 80.0,
        "low": 70.0,
        "might_not_recharge_afterhours": 90.0
    },
    "leftover_power": {
        "almost_charged": 1.0,
        "rapidly_charging": 2.5,
        "rapidly_discharging": -2.5
    },
    "heating": {
        "active_heating_mode": "economy",
        "heating_modes": {
            "keep-alive": {
                "temperature_min": 15.5,
                "temperature_max": 16.0
            },
            "economy": {
                "temperature_min": 21.0,
                "temperature_max": 22.0
            },
            "standard": {
                "temperature_min": 27.5,
                "temperature_max": 28.0
            },
            "comfort": {
                "temperature_min": 33.5,
                "temperature_max": 34.0
            }

        }
    },
    "filtration_scheduler": {
        "period_minutes": 15,
        "control_window_closed_from": 18,
        "control_window_closed_to": 10,
        "minimum_runtime_minutes": 60
    },
    "heating_scheduler": {
        "period_minutes": 5
    }
}
"""

# the actual configuration used to evaluate pool filtration
config = json.loads(default_config, object_hook=lambda d: SimpleNamespace(**d))

# an updated configuration that was just received from MQTT and its synchronization object
updated_config = None
updated_config_sync = Condition()

heating_job = None


class SolarData:

    def __init__(self):
        self.battery_level = 0.0
        self.solar_production = 0.0
        self.local_load = 0.0
        self.export_to_grid = 0.0
        self.battery_charge_power = 0.0
        self.batter_discharge_power = 0.0

    def parse(self, mix_system_status):
        self.battery_level = float(mix_system_status["SOC"])
        self.solar_production = float(mix_system_status["ppv"])
        self.local_load = float(mix_system_status["pLocalLoad"])

        # unused for now but leaving this here for future reference
        self.export_to_grid = float(mix_system_status["pactogrid"])
        self.battery_charge_power = float(mix_system_status["chargePower"])
        self.batter_discharge_power = float(mix_system_status["pdisCharge1"])

    def leftover_power(self):
        return self.solar_production - self.local_load


def get_log_path():
    return os.path.join(str(Path.home()), "log/pool/")


def catch_exceptions(cancel_on_failure=False):
    def catch_exceptions_decorator(job_func):
        @functools.wraps(job_func)
        def wrapper(*args, **kwargs):
            try:
                return job_func(*args, **kwargs)
            except:
                logging.error(traceback.format_exc())
                if cancel_on_failure:
                    return schedule.CancelJob

        return wrapper

    return catch_exceptions_decorator


def on_connect(client, userdata, flags, rc):
    logging.info("Connected to MQTT with result code {}".format(rc))
    client.subscribe("power/config/pool")
    client.subscribe("power/pool/heating")


# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    if msg.topic == "power/config/pool":
        handle_config_update(msg.payload)
    elif msg.topic == "power/pool/heating":
        handle_heating_input_update(msg.payload)


def handle_config_update(message):
    try:
        new_config = json.loads(message, object_hook=lambda d: SimpleNamespace(**d))
    except Exception as ex:
        logging.warning("Failed to parse configuration update message: {}!".format(str(ex)))
        return

    if new_config != config:
        global updated_config

        updated_config_sync.acquire()
        updated_config = new_config
        updated_config_sync.release()


def handle_heating_input_update(message):
    global heating_job
    try:
        input_status_event = json.loads(message, object_hook=lambda d: SimpleNamespace(**d))
        input_status = input_status_event.delta.state
    except Exception as ex:
        logging.warning("Failed to parse heating input state update message: {}!".format(str(ex)))
        return

    if not input_status:
        # Pool unit wants to stop heating the water (water flow stopped most likely) - lets comply

        # Without access to the switch, there is no point continuing ...
        old_switch_status = get_switch_status_guaranteed(args.heating_relay_index)

        logging.info("Received heating input state update! Input state is {}. Current switch status is {}.".format(
            "ON" if input_status else "OFF",
            "ON" if old_switch_status else "OFF"
        ))

        if heating_job is not None:
            schedule.cancel_job(heating_job)
            heating_job = None

        if old_switch_status != input_status:
            if toggle_switch_guaranteed(args.heating_relay_index, current_status=old_switch_status, new_status=input_status):
                logging.info("Stopped heating the pool water based on a request from the control unit!")

    else:
        # Pool unit wants to start heating the water (water flow started most likely)
        if heating_job is None:

            # Run once heating evaluation immediately
            evaluate_pool_water_heating()

            # And then schedule a regular job to keep track of the temperature
            heating_job = schedule.every(config.heating_scheduler.period_minutes).minutes.do(evaluate_pool_water_heating)


def determine_heating_mode():
    if is_window_for_optional_filtration(datetime.now()):
        return "comfort"
    return config.heating.active_heating_mode


def determine_heating_temperatures(heating_mode):
    if heating_mode not in config.heating.heating_modes:
        logging.warning("Invalid heating mode '{}'! Reverting to default keep-alive mode!".format(heating_mode))
        return 15.5, 16.0

    heating_mode_config = config.heating.heating_modes[heating_mode]
    return heating_mode_config.temperature_min, heating_mode_config.temperature_max


@catch_exceptions(cancel_on_failure=False)
def evaluate_pool_water_heating():

    pool_water_temperature = get_pool_water_temperature()
    current_switch_status = get_switch_status(args.heating_relay_index)

    heating_mode = determine_heating_mode()
    min_temp, max_temp = determine_heating_temperatures(heating_mode)

    if pool_water_temperature == float("nan"):
        logging.warning("Unable to evaluate pool water heating! Temperature unavailable! Heating switch is {}".format(
            "ON" if current_switch_status else "OFF"
        ))
        return

    if current_switch_status:
        # Pool water is being heated right now - let's see if we should stop ...
        if pool_water_temperature >= max_temp:
            if toggle_switch(args.heating_relay_index, current_status=current_switch_status, new_status=False):
                logging.info("Stopped heating the pool water! Current temperature is: {} >= {}".format(
                    pool_water_temperature, max_temp
                ))
            else:
                logging.info("Failed to stop heating the pool water! Current temperature is: {} >= {}".format(
                    pool_water_temperature, max_temp
                ))
        else:
            logging.info("Kept heating the pool water! Current temperature is: {} < {}".format(
                pool_water_temperature, max_temp
            ))
    else:
        # Pool water is NOT being heated right now - let's see if we should start ...
        if pool_water_temperature < min_temp:
            if toggle_switch(args.heating_relay_index, current_status=current_switch_status, new_status=True):
                logging.info("Started heating the pool water! Current temperature is: {} < {}".format(
                    pool_water_temperature, min_temp
                ))
            else:
                logging.info("Failed to start heating the pool water! Current temperature is: {} < {}".format(
                    pool_water_temperature, min_temp
                ))
        else:
            logging.info("Kept NOT heating the pool water! Current temperature is: {} >= {}".format(
                pool_water_temperature, min_temp
            ))


def get_pool_water_temperature_guaranteed():
    temperature = float("nan")
    while temperature == float("nan"):
        try:
            temperature = get_pool_water_temperature()
        except:
            time.sleep(5)
    return temperature


def get_pool_water_temperature():
    return asyncio.run(get_pool_water_temperature_async())


async def get_pool_water_temperature_async():
    async with ClientSession() as session:
        account = aioaseko.MobileAccount(session, args.aseko_user, args.aseko_password)
        try:
            await account.login()
        except aioaseko.InvalidAuthCredentials:
            logging.error("The username or password for Aseko Pool Controller is wrong!")
            return

        temperature = await find_pool_water_temperature_async(account)

        await account.logout()

        return temperature


async def find_pool_water_temperature_async(account):
    units = await account.get_units()

    if len(units) != 1:
        logging.error("Expected 1 pool unit but got {} units!".format(len(units)))
        return float("nan")

    if units[0].name != "bazen":
        logging.error("Expected pool unit with name 'bazen' but found '{}' !".format(units[0].name))
        return float("nan")

    bazen = units[0]
    await bazen.get_state()

    if bazen.has_error:
        for error in bazen.errors:
            if error.type == "noFlow":
                logging.warning("{} Unable to retrieve a reliable temperature reading!".format(error.title))
                return float("nan")

    for variable in bazen.variables:
        if variable.name == "Water temp.":
            return variable.current_value


def is_window_for_optional_filtration(timestamp):
    return (timestamp.hour < config.filtration_scheduler.control_window_closed_from) and (
            timestamp.hour > config.filtration_scheduler.control_window_closed_to)


def has_sufficient_power(solar_data):
    # If the battery is almost charged, and we still have power left, let's turn on the filtration
    # Note that 1kW of leftover power will not cover the entire load of pool (0.8kW pump + maybe 3.5kw of heat pump)
    # But better to spend a bit of money to keep the pool clean and warm than give even 1.0kW to grid
    almost_charged = solar_data.battery_level >= config.battery_levels.almost_charged and solar_data.leftover_power() > config.leftover_power.almost_charged

    # If the battery is clearly charging up rapidly, let's start the filtration a bit sooner
    # The idea is that within the next evaluation cycle, the battery might already be charged and
    # It also takes at least 5 more minutes before the heating kicks in
    # This usually happens during the sunny days
    rapidly_charging = solar_data.battery_level >= config.battery_levels.rapidly_charging and solar_data.leftover_power() > config.leftover_power.rapidly_charging

    # TODO: Calculate using the battery_charge_power, battery_level and capacity (4*2.56) to come up with better est.

    return almost_charged or rapidly_charging


def has_insufficient_power(solar_data, after_hours):
    # Note that based on the battery level we have been clearly discharging for some time
    # Also the 2.5kW difference is likely to turn into a surplus (if we have been heating as well) that will help
    # recharge the battery.
    rapidly_discharging = solar_data.battery_level < config.battery_levels.rapidly_discharging and solar_data.leftover_power() < config.leftover_power.rapidly_discharging

    # Or, we already managed to bring the battery level under 70% - let's switch of filtration and start recharging
    battery_low = solar_data.battery_level < config.battery_levels.low

    # Or, we are already outside our optional filtration window (after_hours) which is likely in the evening
    # Then, 90% battery is already not enough
    might_not_recharge = after_hours and solar_data.battery_level < config.battery_levels.might_not_recharge_afterhours

    # TODO: Calculate using the battery_discharge_power and battery_level to come up with better est.

    return rapidly_discharging or battery_low or might_not_recharge


def get_switch_status(relay_index):
    get_switch_status_attempts = 10
    while get_switch_status_attempts > 0:
        try:
            relay_status = shelly.relay(relay_index)
            return relay_status["output"]
        except Exception as ex:
            get_switch_status_attempts -= 1

            logging.warning(
                "Failed to get switch status: {}! Will try again {} times.".format(
                    str(ex),
                    get_switch_status_attempts))

            if get_switch_status_attempts <= 0:
                raise

            time.sleep(5)


def toggle_switch(relay_index, current_status, new_status):
    if current_status == new_status:
        logging.warning("Requested to change switch {} status to {} but switch status already {}. Not doing anything!".format(
            relay_index,
            "ON" if current_status else "OFF",
            "ON" if new_status else "OFF"))

        # switch is already in the requested state, indicate success
        return True

    toggle_attempts = 10
    while (new_status != current_status) and (toggle_attempts > 0):
        try:
            shelly.relay(relay_index, turn=new_status)
            current_status = get_switch_status(relay_index)
        except:
            logging.debug(traceback.format_exc())

        if (new_status != current_status) and (toggle_attempts >= 0):
            toggle_attempts -= 1

            logging.warning(
                "Failed to toggle switch {}! Will try again {} times.".format(
                    relay_index,
                    toggle_attempts))

            time.sleep(5)

    return current_status == new_status


def get_switch_status_guaranteed(relay_index):
    while True:
        try:
            return get_switch_status(relay_index)
        except:
            time.sleep(5)

def toggle_switch_guaranteed(relay_index, current_status, new_status):
    while not toggle_switch(relay_index, current_status, new_status):
        time.sleep(5)

    return True


@catch_exceptions(cancel_on_failure=False)
def evaluate_power_availability():
    # There is a fixed schedule on the Shelly, to run the filtration (pump):
    #   From 7:00 to 9:00
    #   From 20:00 to 22:00
    # Additionally, the pool heating is set to only run:
    #   From 11:00 to 18:00
    # Therefore, the fixed schedule is to ensure the pool water is cleaned up
    # Finally, our window for OPTIONAL filtration (including the heating) is:
    #   From 11:00 to 18:00 (includes intentional 2hrs around the fixed schedule)

    now = datetime.now()
    global filtration_started_at
    after_hours = False

    # Without access to the switch, there is no point continuing ...
    filtration_switch_on = get_switch_status_guaranteed(args.pump_relay_index)
    heating_switch_on = get_switch_status_guaranteed(args.heating_relay_index)

    if not is_window_for_optional_filtration(now):
        if filtration_started_at is None:
            logging.info("Not evaluating available power at this time. Pump switch is {}. Heating switch is {}.".format(
                "ON" if filtration_switch_on else "OFF",
                "ON" if heating_switch_on else "OFF"))
            return
        else:
            after_hours = True

    login_response = growatt_api.login(args.growatt_user, args.growatt_password)
    mix_system_status = growatt_api.mix_system_status(args.mix_id, args.plant_id)
    logging.debug("Current Inverter Values: {}".format(mix_system_status))

    solar_data = SolarData()
    solar_data.parse(mix_system_status)

    if filtration_started_at is None and has_sufficient_power(solar_data):

        if toggle_switch(args.pump_relay_index, current_status=filtration_switch_on, new_status=True):
            filtration_started_at = now

            logging.info("Started filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Pump switch was {}. Heating switch was {}.".format(
                solar_data.leftover_power(),
                solar_data.battery_level,
                "ON" if filtration_switch_on else "OFF",
                "ON" if heating_switch_on else "OFF"
            ))
        else:
            logging.warning(
                "Failed to start filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Pump switch was {}. Heating switch was {}. ".format(
                    solar_data.leftover_power(),
                    solar_data.battery_level,
                    "ON" if filtration_switch_on else "OFF",
                    "ON" if heating_switch_on else "OFF"
                ))

    elif filtration_started_at is not None and has_insufficient_power(solar_data, after_hours):
        filtration_runtime = now - filtration_started_at

        # Let's also give the filtration some time to run, once we started it, even if it is from grid
        if filtration_runtime >= timedelta(minutes=config.filtration_scheduler.minimum_runtime_minutes):
            if toggle_switch(args.pump_relay_index, current_status=filtration_switch_on, new_status=False):
                filtration_started_at = None

                logging.info(
                    "Stopped filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}. Pump switch was {}. Heating switch was {}.".format(
                        solar_data.leftover_power(),
                        solar_data.battery_level,
                        str(filtration_runtime),
                        "ON" if filtration_switch_on else "OFF",
                        "ON" if heating_switch_on else "OFF"
                    ))
            else:
                logging.warning(
                    "Failed to stop filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}. Pump switch was {}. Heating switch was {}.".format(
                        solar_data.leftover_power(),
                        solar_data.battery_level,
                        str(filtration_runtime),
                        "ON" if filtration_switch_on else "OFF",
                        "ON" if heating_switch_on else "OFF"
                    ))
        else:
            logging.info(
                "Kept filtration on! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}, pump switch is {}, heating switch is {}.".format(
                    solar_data.leftover_power(),
                    solar_data.battery_level,
                    str(filtration_runtime),
                    "ON" if filtration_switch_on else "OFF",
                    "ON" if heating_switch_on else "OFF"
                ))

    else:
        logging.info("Kept filtration {}! Leftover solar power {:.2f}kW with {:.2f}% battery level.{} Pump switch is {}. Heating switch is {}.".format(
            "on" if filtration_started_at is not None else "off",
            solar_data.leftover_power(),
            solar_data.battery_level,
            " Runtime: {}.".format(now - filtration_started_at) if filtration_started_at is not None else "",
            "ON" if filtration_switch_on else "OFF",
            "ON" if heating_switch_on else "OFF"
        ))


if __name__ == '__main__':

    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_log_path(), "pool.log"), encoding="utf-8",
                            maxBytes=10485760, backupCount=20),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.INFO,
        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")

    parser = argparse.ArgumentParser(description='Controls the pool filtration pump.')
    parser.add_argument("--growatt_user", dest="growatt_user", type=str,
                        help="User name to connect to the Growatt API")
    parser.add_argument("--growatt_password", dest="growatt_password", type=str,
                        help="Password to connect to the Growatt API")
    parser.add_argument("--plant_id", dest="plant_id", type=str,
                        help="Plant ID of the Solar Plant withing Growatt API")
    parser.add_argument("--mix_id", dest="mix_id", type=str,
                        help="Mix ID of the Inventor withing Growatt API")
    parser.add_argument("--relay_ip_address", dest="relay_ip", type=str,
                        help="IP Address of the Shelly relay controlling the pump.")
    parser.add_argument("--pump_relay_index", dest="pump_relay_index", type=int,
                        help="Relay index of the Shelly relay controlling the pump.")
    parser.add_argument("--heating_relay_index", dest="heating_relay_index", type=int,
                        help="Relay index of the Shelly relay controlling the heating.")
    parser.add_argument("--filtration_started_at", dest="fsa", type=str,
                        help="Specify when the filtration was started at (useful for restarts)")
    parser.add_argument("--mqtt_ip", dest="mqtt_ip", type=str,
                        help="IP Address of the MQTT broker for various integrations.")
    parser.add_argument("--mqtt_port", dest="mqtt_port", type=int,
                        help="Port of the MQTT broker for various integrations.")
    parser.add_argument("--aseko_user", dest="aseko_user", type=str,
                        help="User name to connect to the Aseko API")
    parser.add_argument("--aseko_password", dest="aseko_password", type=str,
                        help="Password to connect to the Aseko API")

    args = parser.parse_args()

    if args.fsa is not None and args.fsa != '':
        filtration_started_at = datetime.fromisoformat(args.fsa)

    growatt_api = growattServer.GrowattApi(False, args.growatt_user)
    growatt_api.server_url = r"https://server.growatt.com/"

    while shelly is None:
        try:
            shelly = ShellyPy.Shelly(args.relay_ip)

        except Exception as ex:
            logging.error("Failed to initialize Shelly: {}. Will keep trying ...".format(str(ex)))
            time.sleep(5)

    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message
    mqtt_client.connect(args.mqtt_ip, args.mqtt_port, 60)

    mqtt_client.loop_start()

    # Then, schedule a job to check the Solar status every 15 minutes
    filtration_job = schedule.every(config.filtration_scheduler.period_minutes).minutes.do(evaluate_power_availability)

    # Run the job immediately after a startup
    schedule.run_all()

    # And finally, according to a schedule
    while True:
        schedule.run_pending()

        # Did we get a new configuration from MQTT?
        if updated_config is not None:
            updated_config_sync.acquire()

            # Log the old and new configuration for convenient comparison
            logging.info("Old configuration: {}".format(config))
            logging.info("New configuration: {}".format(updated_config))

            # Update the configuration
            config = updated_config
            updated_config = None

            updated_config_sync.release()

            # Reschedule the jobs (in case the period has changed)
            schedule.cancel_job(filtration_job)
            filtration_job = schedule.every(config.filtration_scheduler.period_minutes).minutes.do(evaluate_power_availability)

            if heating_job is not None:
                schedule.cancel_job(heating_job)
                heating_job = schedule.every(config.heating_scheduler.period_minutes).minutes.do(evaluate_pool_water_heating)

            # And finally, run immediately, just to recheck with the new configuration
            schedule.run_all()

        # Just to prevent a tight-loop
        time.sleep(1)

    mqtt_client.loop_stop(force=False)

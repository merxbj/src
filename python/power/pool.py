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
    "scheduler": {
        "period_minutes": 15,
        "control_window_closed_from": 18,
        "control_window_closed_to": 10,
        "minimum_runtime_minutes": 60
    }
}
"""

# the actual configuration used to evaluate pool filtration
config = json.loads(default_config, object_hook=lambda d: SimpleNamespace(**d))

# an updated configuration that was just received from MQTT and its synchronization object
updated_config = None
updated_config_sync = Condition()

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
    client.subscribe("power/config")


# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    try:
        new_config = json.loads(msg.payload, object_hook=lambda d: SimpleNamespace(**d))
    except Exception as ex:
        logging.warning("Failed to parse configuration update message: {}!".format(str(ex)))
        return

    if hasattr(new_config, "Pool"):
        new_config = new_config.Pool
        if new_config != config:
            global updated_config

            updated_config_sync.acquire()
            updated_config = new_config
            updated_config_sync.release()


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


def get_switch_status():
    get_switch_status_attempts = 10
    while get_switch_status_attempts >= 0:
        try:
            relay_status = shelly.relay(args.relay_index)
            return relay_status["output"]
        except Exception as ex:
            get_switch_status_attempts -= 1

            logging.warning(
                "Failed to get switch status: {}! Will try again {} times.".format(
                    str(ex),
                    get_switch_status_attempts))

            if get_switch_status_attempts < 0:
                raise

            time.sleep(5)


def toggle_switch(current_status, new_status):
    if current_status == new_status:
        logging.warning("Requested to change switch status to {} but switch status already {}. Not doing anything!".format(
        "ON" if current_status else "OFF",
        "ON" if new_status else "OFF"))
        return

    toggle_attempts = 10
    while (new_status != current_status) and (toggle_attempts >= 0):
        try:
            shelly.relay(args.relay_index, turn=new_status)
            current_status = get_switch_status()
        except:
            logging.debug(traceback.format_exc())

        if (new_status != current_status) and (toggle_attempts >= 0):
            toggle_attempts -= 1

            logging.warning(
                "Failed to toggle switch! Will try again {} times.".format(
                    toggle_attempts))

            time.sleep(5)

    return current_status == new_status


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
    while True:
        try:
            switch_on = get_switch_status()
            break
        except:
            time.sleep(5)

    if now.hour >= config.scheduler.control_window_closed_from or now.hour <= config.scheduler.control_window_closed_to:
        if filtration_started_at is None:
            logging.info("Not evaluating available power at this time. Switch is {}.".format(
                "ON" if switch_on else "OFF"))
            return
        else:
            after_hours = True

    login_response = growatt_api.login(args.growatt_user, args.growatt_password)
    mix_system_status = growatt_api.mix_system_status(args.mix_id, args.plant_id)
    logging.debug("Current Inverter Values: {}".format(mix_system_status))

    solar_data = SolarData()
    solar_data.parse(mix_system_status)

    if filtration_started_at is None and has_sufficient_power(solar_data):

        if toggle_switch(current_status=switch_on, new_status=True):
            filtration_started_at = now

            logging.info("Started filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Switch was {}.".format(
                solar_data.leftover_power(),
                solar_data.battery_level,
                "ON" if switch_on else "OFF"
            ))
        else:
            logging.warning(
                "Failed to start filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Switch was {}.".format(
                    solar_data.leftover_power(),
                    solar_data.battery_level,
                    "ON" if switch_on else "OFF"
                ))

    elif filtration_started_at is not None and has_insufficient_power(solar_data, after_hours):
        filtration_runtime = now - filtration_started_at

        # Let's also give the filtration some time to run, once we started it, even if it is from grid
        if filtration_runtime >= timedelta(minutes=config.scheduler.minimum_runtime_minutes):
            if toggle_switch(current_status=switch_on, new_status=False):
                filtration_started_at = None

                logging.info(
                    "Stopped filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}. Switch was {}.".format(
                        solar_data.leftover_power(),
                        solar_data.battery_level,
                        str(filtration_runtime),
                        "ON" if switch_on else "OFF"
                    ))
            else:
                logging.warning(
                    "Failed to stop filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}. Switch was {}.".format(
                        solar_data.leftover_power(),
                        solar_data.battery_level,
                        str(filtration_runtime),
                        "ON" if switch_on else "OFF"
                    ))
        else:
            logging.info(
                "Kept filtration on! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}, switch was {}.".format(
                    solar_data.leftover_power(),
                    solar_data.battery_level,
                    str(filtration_runtime),
                    "ON" if switch_on else "OFF"
                ))

    else:
        logging.info("Kept filtration {}! Leftover solar power {:.2f}kW with {:.2f}% battery level.{} Switch is {}.".format(
            "on" if filtration_started_at is not None else "off",
            solar_data.leftover_power(),
            solar_data.battery_level,
            " Runtime: {}.".format(now - filtration_started_at) if filtration_started_at is not None else "",
            "ON" if switch_on else "OFF"
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
    parser.add_argument("-gu" "--growatt_user", dest="growatt_user", type=str,
                        help="User name to connect to the Growatt API")
    parser.add_argument("-gp" "--growatt_password", dest="growatt_password", type=str,
                        help="Password to connect to the Growatt API")
    parser.add_argument("-pi" "--plant_id", dest="plant_id", type=str,
                        help="Plant ID of the Solar Plant withing Growatt API")
    parser.add_argument("-mi" "--mix_id", dest="mix_id", type=str,
                        help="Mix ID of the Inventor withing Growatt API")
    parser.add_argument("-rip" "--relay_ip_address", dest="relay_ip", type=str,
                        help="IP Address of the Shelly relay controlling the pump.")
    parser.add_argument("-ridx" "--relay_index", dest="relay_index", type=int,
                        help="Relay index of the Shelly relay controlling the pump.")
    parser.add_argument("-fsa" "--filtration_started_at", dest="fsa", type=str,
                        help="Specify when the filtration was started at (useful for restarts)")
    parser.add_argument("-qip" "--mqtt_ip", dest="mqtt_ip", type=str,
                        help="IP Address of the MQTT broker for Power system configuration.")
    parser.add_argument("-qp" "--mqtt_port", dest="mqtt_port", type=int,
                        help="Port of the MQTT broker for Power system configuration.")

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
    main_job = schedule.every(config.scheduler.period_minutes).minutes.do(evaluate_power_availability)

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

            # Reschedule the main job (in case the period has changed)
            schedule.cancel_job(main_job)
            main_job = schedule.every(config.scheduler.period_minutes).minutes.do(evaluate_power_availability)

            # And finally, run immediately, just to recheck with the new configuration
            schedule.run_all()

        # Just to prevent a tight-loop
        time.sleep(1)

    mqtt_client.loop_stop(force=False)

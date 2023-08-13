import argparse
from datetime import datetime, timedelta
import logging
import os
import sys
import traceback
from pathlib import Path
from logging.handlers import RotatingFileHandler

import growattServer
import ShellyPy
import schedule
import time
import functools

# let's define the Growatt API on a global level for easier access
growatt_api = growattServer.GrowattApi()
growatt_api.server_url = r"https://server.growatt.com/"

# let's also the Shelly API on a global level for easier access
shelly = None

filtration_started_at = None


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


def has_sufficient_power(solar_data):
    # If the battery is almost charged, and we still have power left, let's turn on the filtration
    # Note that 1kW of leftover power will not cover the entire load of pool (0.8kW pump + maybe 3.5kw of heat pump)
    # But better to spend a little bit of money to keep the pool clean and warm than give even 1.0kW to grid
    almost_charged = solar_data.battery_level >= 95.0 and solar_data.leftover_power() > 1.0

    # If the battery is clearly charging up rapidly, let's start the filtration a little bit sooner
    # The idea is that within the next evaluation cycle, the battery might already be charged and
    # It also takes at least 5 more minutes before the heating kicks in
    # This usually happens during the sunny days
    charging_rapidly = solar_data.battery_level >= 85.0 and solar_data.leftover_power() > 2.5

    # TODO: Calculate using the battery_charge_power, battery_level and capacity (4*2.56) to come up with better est.

    return almost_charged or charging_rapidly


def has_insufficient_power(solar_data, after_hours):
    # Note that based on the battery level we have been clearly discharging for some time
    # Also the 2.5kW difference is likely to turn into a surplus (if we have been heating as well) that will help
    # recharge the battery.
    discharging_rapidly = solar_data.battery_level < 80.0 and solar_data.leftover_power() < -2.5

    # Or, we already managed to bring the battery level under 70% - let's switch of filtration and start recharging
    battery_low = solar_data.battery_level < 70.0

    # Or, we are already outside our optional filtration window (after_hours) which is likely in the evening
    # Then, 90% battery is already not enough
    might_not_recharge = after_hours and solar_data.battery_level < 90.0

    # TODO: Calculate using the battery_discharge_power and battery_level to come up with better est.

    return discharging_rapidly or battery_low or might_not_recharge


def get_switch_status():
    relay_status = shelly.relay(args.relay_index)
    return relay_status["output"]

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
            logging.error(traceback.format_exc())

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

    switch_on = get_switch_status()

    if now.hour >= 18 or now.hour <= 10:
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

        # Make sure we run the filtration for at least 45 minutes (at 18:00 the window closes)
        if now.hour == 17 and now.minute > 15:
            logging.info("It's too late! Leftover solar power {:.2f}kW with {:.2f}% battery level. Switch is {}.".format(
                solar_data.leftover_power(),
                solar_data.battery_level,
                "ON" if switch_on else "OFF"
            ))
            return

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
        if filtration_runtime >= timedelta(hours=1):
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

    args = parser.parse_args()

    if args.fsa is not None and args.fsa != '':
        filtration_started_at = datetime.fromisoformat(args.fsa)

    while shelly is None:
        try:
            shelly = ShellyPy.Shelly(args.relay_ip)
        except Exception as ex:
            logging.error("Failed to initialize Shelly: {}. Will keep trying ...".format(str(ex)))

    # Then, schedule a job to check the Solar status every 15 minutes
    schedule.every(15).minutes.do(evaluate_power_availability)

    # Run the job immediately after a startup
    schedule.run_all()

    # And finally, according to a schedule
    while True:
        schedule.run_pending()
        time.sleep(1)

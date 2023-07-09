import argparse
from datetime import datetime, timedelta
import logging
import os
import sys
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

filtration_started_at = None


def get_log_path():
    return os.path.join(str(Path.home()), "log/pool/")


def catch_exceptions(cancel_on_failure=False):
    def catch_exceptions_decorator(job_func):
        @functools.wraps(job_func)
        def wrapper(*args, **kwargs):
            try:
                return job_func(*args, **kwargs)
            except:
                import traceback
                print(traceback.format_exc())
                if cancel_on_failure:
                    return schedule.CancelJob
        return wrapper
    return catch_exceptions_decorator


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

    if filtration_started_at is None and now.hour >= 18 or now.hour <= 10:
        logging.info("Not evaluating available power at this time ...")
        return

    login_response = growatt_api.login(args.growatt_user, args.growatt_password)
    mix_system_status = growatt_api.mix_system_status(args.mix_id, args.plant_id)
    logging.debug("Current Inverter Values: {}".format(mix_system_status))

    battery_level = float(mix_system_status["SOC"])
    solar_production = float(mix_system_status["ppv"])
    local_load = float(mix_system_status["pLocalLoad"])

    # unused for now but leaving this here for future reference
    #export_to_grid = float(mix_system_status["pactogrid"])
    #battery_charge_power = float(mix_system_status["chargePower"])
    #batter_discharge_power = float(mix_system_status["pdisCharge1"])

    # If the battery is almost charged, and we still have power left, let's turn on the filtration
    # Note that 1kw of leftover power will not cover the entire load of pool (800W pump + maybe 3.5kw of heat pump)
    # But better to spend a little bit of money to keep the pool clean and warm than give even 1.0kW to grid
    if filtration_started_at is None and battery_level >= 95.0 and (solar_production - local_load) > 1.0:

        # Make sure we run the filtration for at least 45 minutes (at 18:00 the window closes)
        if now.hour == 17 and now.minute > 15:
            logging.info("It's too late! Leftover solar power {:.2f}kW with {:.2f}% battery level.".format(
                solar_production - local_load,
                battery_level
            ))
            return

        device = ShellyPy.Shelly(args.relay_ip)
        device.relay(0, turn=True)
        filtration_started_at = now

        logging.info("Started filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level.".format(
            solar_production - local_load,
            battery_level
        ))

    # If the filtration has already been started but there is not enough leftover power, consider stopping it
    # Note that based on the battery level we have been clearly discharging for some time
    # Also the 2.5kW difference is likely to turn into a surplus (if we have been heating as well) that will help
    # recharge the battery.
    elif filtration_started_at is not None and battery_level < 80.0 and (solar_production - local_load) < -2.5:
        filtration_runtime = now - filtration_started_at

        # Let's also give the filtration some time to run, once we started it, even if it is from grid
        if filtration_runtime >= timedelta(hours=1):
            device = ShellyPy.Shelly(args.relay_ip)
            device.relay(0, turn=False)
            filtration_started_at = None

            logging.info("Stopped filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}".format(
                solar_production - local_load,
                battery_level,
                str(filtration_runtime)
            ))
        else:
            logging.info("Kept filtration on! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}".format(
                solar_production - local_load,
                battery_level,
                str(filtration_runtime)
            ))
            
    # If the filtration has already been started but there is not enough leftover power, consider stopping it
    # Note that based on the battery level we have been clearly discharging for some time
    # In this case, we already managed to bring the battery level under 70% - let's switch of filtration and start recharging
    elif filtration_started_at is not None and battery_level < 70.0:
        filtration_runtime = now - filtration_started_at

        # Let's also give the filtration some time to run, once we started it, even if it is from grid
        if filtration_runtime >= timedelta(hours=1):
            device = ShellyPy.Shelly(args.relay_ip)
            device.relay(0, turn=False)
            filtration_started_at = None

            logging.info("Stopped filtration! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}".format(
                solar_production - local_load,
                battery_level,
                str(filtration_runtime)
            ))
        else:
            logging.info("Kept filtration on! Leftover solar power {:.2f}kW with {:.2f}% battery level. Runtime: {}".format(
                solar_production - local_load,
                battery_level,
                str(filtration_runtime)
            ))
            
    else:
        logging.info("Kept filtration {}! Leftover solar power {:.2f}kW with {:.2f}% battery level.{}".format(
                "on" if filtration_started_at is not None else "off",
                solar_production - local_load,
                battery_level,
                " Runtime: {}".format(now - filtration_started_at) if filtration_started_at is not None else ""
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
    parser.add_argument("-fsa" "--filtration_started_at", dest="fsa", type=str,
                        help="Specify when the filtration was started at (useful for restarts)")

    args = parser.parse_args()
    
    if args.fsa is not None and args.fsa != '':
        filtration_started_at = datetime.fromisoformat(args.fsa)

    # Then, schedule a job to check the Solar status every 15 minutes
    schedule.every(15).minutes.do(evaluate_power_availability)

    # Run the job immediately after a startup
    schedule.run_all()

    # And finally, according to a schedule
    while True:
        schedule.run_pending()
        time.sleep(1)

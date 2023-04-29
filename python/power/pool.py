import argparse
import logging
import os
import sys
from pathlib import Path

import growattServer
import ShellyPy


def get_log_path():
    return os.path.join(str(Path.home()), "log/pool/")


if __name__ == '__main__':

    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        logging.FileHandler(os.path.join(get_log_path(), "pool.log"), encoding="utf-8"),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.DEBUG,
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

    args = parser.parse_args()

    api = growattServer.GrowattApi()
    api.server_url = r"https://server.growatt.com/"
    login_response = api.login(args.growatt_user, args.growatt_password)

    print(api.mix_system_status(args.mix_id, args.plant_id))

    device = ShellyPy.Shelly(args.relay_ip)
    device.relay(0, turn=False)

    # 'ppv': '0.24', 'pactogrid': '0.03', 'SOC': '42', 'pLocalLoad': '0.72' 'pdisCharge1': '0.19' 'chargePower': '0'
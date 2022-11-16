import os
import sys

import websocket
import json
import requests
import base64
import argparse
import logging

from pathlib import Path

sensor = 0


def get_log_path():
    return os.path.join(str(Path.home()), "log/meteo/")


def decode_fixed_decimal(hex_encoded):
    number = int(hex_encoded, base=16)
    base = number & 0x7FF                  # bits 0 - 10 are the base
    scale = number >> 11 & 0xF             # bits 11 - 14 are scaling factor
    if 0x8000 & number:                    # bit 15 is sign
        base = -((0x7FF & ~number) + 1)    # negative numbers are 1's complement
    value = .01 * base * (1 << scale)      # base is divided by 100 and further divided by the scaling factor
    return value


def on_message(wsapp, message):

    j = json.loads(message)

    if j['dstraw'] == sensor:
        logging.debug(message)
        logging.info("Temperature Change: {}C".format(str(decode_fixed_decimal(j['datahex']))))

    if j['dst'] == dst:
        logging.debug(message)
        logging.info("Wind Speed Change: {}m/s".str(decode_fixed_decimal(j['datahex'])))


def get_initial_values(host, user_name, password):
    x = (user_name + ":" + password)
    header = {'Authorization': "Basic " + base64.b64encode(x.encode()).decode()}
    r = requests.post(f"http://{host}/apps/localbus.lp?", headers=header)
    return r.content


def get_sensor_value(data):
    for obj in data['objects']:
        if obj['id'] == sensor:
            return decode_fixed_decimal(obj['datahex'])


def get_dst_value(data):
    for obj in data['objects']:
        if obj['id'] == 12290:
            return decode_fixed_decimal(obj['datahex'])


if __name__ == '__main__':

    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        logging.FileHandler(os.path.join(get_log_path(), "meteo.log")),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.DEBUG,
        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")

    parser = argparse.ArgumentParser(description='Monitor meteorological data.')
    parser.add_argument("-s" "--sensor", dest="sensor", default="12289", type=int,
                        help="Sensor to pull data from")
    parser.add_argument("-d" "--dst", dest="dst", default="6\\/0\\/2", type=str,
                        help="Sensor to pull data from")
    parser.add_argument("-u" "--user", dest="user", type=str,
                        help="User name to connect to the meteorological station")
    parser.add_argument("-p" "--password", dest="password", type=str,
                        help="Password to connect to the meteorological station")
    parser.add_argument("--host", dest="host", type=str,
                        help="hostname[:port] of the meteorological station")

    args = parser.parse_args()

    sensor = args.sensor
    dst = args.dst

    payload = json.loads(get_initial_values(args.host, args.user, args.password))
    logging.info("Initial Temperature: {}C".format(get_sensor_value(payload)))
    logging.info("Initial Wind Speed: {}m/s".format(get_dst_value(payload)))

    auth = payload["auth"]
    wsapp = websocket.WebSocketApp(f"ws://{args.host}/apps/localbus.lp?auth={auth}", on_message=on_message)
    wsapp.run_forever()

import os
import sys

import websocket
import json
import requests
import base64
import argparse
import logging

from pathlib import Path

objects = {}


def get_log_path():
    return os.path.join(str(Path.home()), "log/hm/")


def decode_fixed_decimal(hex_encoded):
    number = int(hex_encoded, base=16)
    base = number & 0x7FF                  # bits 0 - 10 are the base
    scale = number >> 11 & 0xF             # bits 11 - 14 are scaling factor
    if 0x8000 & number:                    # bit 15 is sign
        base = -((0x7FF & ~number) + 1)    # negative numbers are 1's complement
    value = .01 * base * (1 << scale)      # base is divided by 100 and further divided by the scaling factor
    return value


def decode_object_value(hex_encoded, datatype):
    if datatype == 1:
        return "Off" if int(hex_encoded) == 0 else "On"
    elif datatype == 9:
        return decode_fixed_decimal(hex_encoded)
    else:
        return hex_encoded


def on_message(wsapp, message):
    msg = json.loads(message)
    address = msg["dst"].replace("\\", "")
    if address in objects:
        object_info = objects[address]
        object_value = decode_object_value(msg["datahex"], object_info["datatype"])
        logging.info("Object: {} -> Value: {}".format(object_info["path"], object_value))
    else:
        logging.warning("Unknown address {}: {}".format(address, message))


def get_initial_values(host, user_name, password):
    x = (user_name + ":" + password)
    header = {'Authorization': "Basic " + base64.b64encode(x.encode()).decode()}
    r = requests.post(f"http://{host}/apps/localbus.lp?", headers=header)
    return r.content


def get_home_config(host, user_name, password):
    x = (user_name + ":" + password)
    header = {'Authorization': "Basic " + base64.b64encode(x.encode()).decode()}
    r = requests.post(f"http://{host}/apps/data/touch/api/?a=config", headers=header)
    return r.content


def preprocess_home_config(cfg):
    for floor_config in cfg["floors"]:
        floor_name = floor_config["title"]
        for room_config in floor_config["rooms"]:
            room_name = room_config["title"]
            for widget_config in room_config["widgets"]:
                widget_name = widget_config["title"]
                for object_config in widget_config["objects"].values():
                    object_name = object_config["name"]
                    object_address = object_config["address"]
                    object_datatype = object_config["datatype"]

                    object_path = "{} / {} / {} / {}".format(floor_name, room_name, widget_name, object_name)
                    objects[object_address] = {"path": object_path, "datatype": object_datatype}


def get_object_config(host, user_name, password):
    x = (user_name + ":" + password)
    header = {'Authorization': "Basic " + base64.b64encode(x.encode()).decode()}
    r = requests.post(f"http://{host}/apps/data/touch/api/?a=objects&dt=%5B%5D", headers=header)
    return r.content


def preprocess_object_config(cfg):
    for object_config in cfg:
        object_address = object_config["id"]
        object_name = object_config["text"].replace(object_address, "").strip()
        object_datatype = object_config["datatype"]

        if object_address not in objects:
            logging.debug("Object {} on address {} not placed into any room.".format(object_name, object_address))
            objects[object_address] = {"path": object_name, "datatype": object_datatype}


if __name__ == '__main__':

    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        logging.FileHandler(os.path.join(get_log_path(), "hm.log"), encoding="utf-8"),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.DEBUG,
        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")

    parser = argparse.ArgumentParser(description='Monitor meteorological data.')
    parser.add_argument("-u" "--user", dest="user", type=str,
                        help="User name to connect to the meteorological station")
    parser.add_argument("-p" "--password", dest="password", type=str,
                        help="Password to connect to the meteorological station")
    parser.add_argument("--host", dest="host", type=str,
                        help="hostname[:port] of the meteorological station")

    args = parser.parse_args()

    logging.debug("Getting initial values ...")
    payload = json.loads(get_initial_values(args.host, args.user, args.password))

    logging.debug("Getting home configuration ...")
    home_cfg = json.loads(get_home_config(args.host, args.user, args.password))
    preprocess_home_config(home_cfg)

    logging.debug("Getting object configuration ...")
    object_cfg = json.loads(get_object_config(args.host, args.user, args.password))
    preprocess_object_config(object_cfg)

    logging.debug("Starting the web socket client ...")
    wsapp = websocket.WebSocketApp(f"ws://{args.host}/apps/localbus.lp?auth={payload['auth']}", on_message=on_message)
    wsapp.run_forever()

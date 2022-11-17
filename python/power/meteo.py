import os
import sys

import websocket
import json
import requests
import base64
import argparse
import logging

# date & time stuff
from datetime import datetime, timedelta
from dateutil import tz

# threading
from threading import Thread
from threading import Condition

# database
import mariadb

from pathlib import Path

messages = []
messages_event = Condition()

monitored = ["6/0/1", "6/0/2", "6/0/0"]
objects = {}


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


def decode_object_value(hex_encoded, datatype):
    if datatype == 1:
        return "Off" if int(hex_encoded) == 0 else "On"
    elif datatype == 9:
        return decode_fixed_decimal(hex_encoded)
    else:
        return hex_encoded


def on_message(wsapp, message):
    if len(messages) >= 552000:
        del messages[0]

    messages_event.acquire()
    messages.append({"values": (message, datetime.utcnow().replace(tzinfo=tz.tzutc())), "handled": False})
    messages_event.notify()
    messages_event.release()


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
        for room_config in floor_config["rooms"]:
            for widget_config in room_config["widgets"]:
                for object_config in widget_config["objects"].values():
                    object_name = object_config["name"]
                    object_address = object_config["address"]
                    object_datatype = object_config["datatype"]

                    objects[object_address] = {"name": object_name, "datatype": object_datatype}


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
            objects[object_address] = {"name": object_name, "datatype": object_datatype}


def log_message(msg):
    address = msg["dst"].replace("\\", "")
    object_info = objects[address]
    object_value = decode_object_value(msg["datahex"], object_info["datatype"])

    logging.info("Object: {} -> Value: {}".format(object_info["name"], object_value))


def store_message(msg, timestamp, db_conn):
    address = msg["dst"].replace("\\", "")
    object_info = objects[address]
    object_value = decode_object_value(msg["datahex"], object_info["datatype"])

    cur = db_conn.cursor()
    cur.execute("INSERT INTO meteo_event VALUES(?, ?, ?)", (address, timestamp, object_value))
    cur.close()
    db_conn.commit()


def register_available_date(msg, timestamp, db_conn):
    address = msg["dst"].replace("\\", "")
    cur = db_conn.cursor()
    cur.execute("REPLACE INTO available_date VALUES(?, ?)", (address, timestamp.date()))
    cur.close()
    db_conn.commit()


def db_thread():
    db_conn = create_connection()
    while True:
        messages_event.acquire()
        messages_event.wait()

        messages_to_handle = []

        for index in range(len(messages) - 1, -1, -1):
            if not messages[index]["handled"]:
                messages_to_handle.insert(0, messages[index]["values"])
                messages[index]["handled"] = True

        messages_event.release()

        for message in messages_to_handle:
            msg_raw, timestamp = message
            msg = json.loads(msg_raw)
            address = msg["dst"].replace("\\", "")
            if address in monitored and address in objects:
                store_message(msg, timestamp, db_conn)
                register_available_date(msg, timestamp, db_conn)
                log_message(msg)


def create_connection():
    return mariadb.connect(host='localhost', database='power')


def setup_database():
    con = create_connection()

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS `meteo_event` (
                      `address`      varchar(20) NOT NULL,
                      `timestamp`   datetime(6) NOT NULL,
                      `value`       double(10,2) NOT NULL,
                      PRIMARY KEY (`address`,`timestamp`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)

    cur.close()
    con.commit()

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS `meteo_event_address` (
                      `address`      varchar(12) NOT NULL,
                      `description` varchar(100) DEFAULT NULL,
                      PRIMARY KEY (`address`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS `available_date` (
                      `address`          int(11) NOT NULL,
                      `available_date`   date NOT NULL,
                      PRIMARY KEY (`address`, `available_date`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)
    cur.close()
    con.commit()

    con.close()


def register_monitored_objects():
    db_conn = create_connection()
    for address in monitored:
        if address in objects:
            object_info = objects[address]

            logging.debug("Registering {} at {}".format(object_info["name"], address))

            cur = db_conn.cursor()
            cur.execute("REPLACE INTO meteo_event_address VALUES(?, ?)", (address, object_info["name"]))
            cur.close()
            db_conn.commit()



if __name__ == '__main__':

    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        logging.FileHandler(os.path.join(get_log_path(), "meteo.log"), encoding="utf-8"),
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

    logging.info("Setting up the database ...")
    setup_database()

    logging.info("Setting up the database update thread ...")
    db_thread = Thread(target=db_thread)
    db_thread.start()

    logging.debug("Getting initial values ...")
    payload = json.loads(get_initial_values(args.host, args.user, args.password))

    logging.debug("Getting home configuration ...")
    home_cfg = json.loads(get_home_config(args.host, args.user, args.password))
    preprocess_home_config(home_cfg)

    logging.debug("Getting object configuration ...")
    object_cfg = json.loads(get_object_config(args.host, args.user, args.password))
    preprocess_object_config(object_cfg)

    logging.debug("Registering monitored objects ...")
    register_monitored_objects()

    logging.debug("Starting the web socket client ...")
    wsapp = websocket.WebSocketApp(f"ws://{args.host}/apps/localbus.lp?auth={payload['auth']}", on_message=on_message)
    wsapp.run_forever()

    logging.info("Monitoring meteo updates ... ")

    db_thread.join()

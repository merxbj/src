import os
import sys
from pathlib import Path
import argparse

import logging
from logging.handlers import RotatingFileHandler

import paho.mqtt.client as mqtt


mqtt_client = mqtt.Client()


def get_log_path():
    return os.path.join(str(Path.home()), "log/shelly/")


# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    logging.info("Connected with result code {}".format(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("shellypro1pm-ec62608ab640/debug/log")
    client.subscribe("power/pool/#")


# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    logger = logging.getLogger(msg.topic)
    logger.info(msg.payload)


if __name__ == '__main__':

    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_log_path(), "shelly.log"), encoding="utf-8",
                            maxBytes=10485760, backupCount=20),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.INFO,
        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")

    parser = argparse.ArgumentParser(description='Monitors various Shelly MQTT topics and logs them in file.')
    parser.add_argument("-mip" "--mqtt_ip", dest="mqtt_ip", type=str,
                        help="IP Address of the MQTT broker for Shelly.")
    parser.add_argument("-mp" "--mqtt_port", dest="mqtt_port", type=int,
                        help="Port of the MQTT broker for Shelly.")

    args = parser.parse_args()

    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message
    mqtt_client.connect(args.mqtt_ip, args.mqtt_port, 60)

    # Blocking call that processes network traffic, dispatches callbacks and
    # handles reconnecting.
    # Other loop*() functions are available that give a threaded interface and a
    # manual interface.
    mqtt_client.loop_forever()

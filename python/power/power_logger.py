import os
import sys
from pathlib import Path
import argparse

import logging
from logging.handlers import RotatingFileHandler

import paho.mqtt.client as mqtt


mqtt_client = mqtt.Client()


def get_log_path():
    return os.path.join(str(Path.home()), "log/mqtt/")


# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    logging.info("Connected with result code {}".format(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("shellypro1pm-ec62608ab640/debug/log")
    client.subscribe("shellypro4pm-c8f09e83efa8/debug/log")
    client.subscribe("shellyplusht-b0b21c135e18/status/temperature:0")
    client.subscribe("shellyplusht-b0b21c135e18/status/humidity:0")
    client.subscribe("shellies/shellydw2-D741F2/debug/log")
    client.subscribe("power/log/#")


def parse_log_level(log_level_str):
    if log_level_str == "debug":
        return logging.DEBUG
    elif log_level_str == "info":
        return logging.INFO
    elif log_level_str == "error":
        return logging.ERROR
    elif log_level_str == "warn":
        return logging.WARN
    else:
        return logging.DEBUG


# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    module = str(msg.topic)
    log_level = logging.DEBUG

    if module.startswith("power/log"):
        topic_path_components = module.split("/")
        if len(topic_path_components) == 4:
            module = topic_path_components[3]
            log_level = parse_log_level(topic_path_components[2])

    logger = logging.getLogger(module)
    logger.log(log_level, msg.payload.decode("utf-8"))


if __name__ == '__main__':

    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_log_path(), "mqtt.log"), encoding="utf-8",
                            maxBytes=10485760, backupCount=20),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.DEBUG,
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

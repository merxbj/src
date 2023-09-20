#!/bin/sh
mosquitto_pub -h localhost -t power/config/pool -r -f /home/pi/config/pool_config.json
mosquitto_pub -h localhost -t power/config/dehumidifier -r -f /home/pi/config/dehumidifier_config.json
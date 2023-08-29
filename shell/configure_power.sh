#!/bin/sh
mosquitto_pub -h localhost -t power/config -r -f /home/pi/power/config.json
#!/bin/sh
/usr/bin/screen -dmS power-web python3 /home/pi/power/web.py
sleep 10s
/usr/bin/screen -dmS power-house python3 /home/pi/power/s0.py -p 21
sleep 5s
/usr/bin/screen -dmS power-hp python3 /home/pi/power/s0.py -p 16
sleep 5s
/usr/bin/screen -dmS meteo python3 /home/pi/power/meteo.py -u vizudum -p vizudum --host 192.168.88.120
sleep 5s
/usr/bin/screen -dmS hm python3 /home/pi/power/homemonitor.py -u vizudum -p vizudum --host 192.168.88.120
sleep 5s
/usr/bin/screen -dmS pool python3.11 --growatt_user "user" --growatt_password "password" --mix_id "TPJ4CD602H" --plant_id "1419399" --relay_ip_address "192.168.88.73" --pump_relay_index 0 --heating_relay_index 2 --mqtt_ip 192.168.88.48 --mqtt_port 1883 --aseko_user "user" --aseko_password "password"
sleep 5s
/usr/bin/screen -dmS shelly python3 /home/pi/power/shelly.py -mip 192.168.88.48  -mp 1883

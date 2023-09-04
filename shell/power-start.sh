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
/usr/bin/screen -dmS pool python3 /home/pi/power/pool.py -gu "user" -gp "password" -mi "TPJ4CD602H" -pi "1419399" -rip "192.168.88.73" -ridx 0 -qip 192.168.88.48  -qp 1883
sleep 5s
/usr/bin/screen -dmS shelly python3 /home/pi/power/shelly.py -mip 192.168.88.48  -mp 1883

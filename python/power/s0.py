import datetime
import os
from dateutil import tz
import pigpio
from threading import Thread
from threading import Condition
import mariadb
from pathlib import Path
import argparse

pulses = []
pulse_event = Condition()


def callback(g, l, t):
    if len(pulses) >= 552000:
        del pulses[0]

    pulse_event.acquire()
    pulses.append({"values": (g, l, t, datetime.datetime.utcnow().replace(tzinfo=tz.tzutc())), "handled": False})
    pulse_event.notify()
    pulse_event.release()


def setup_pulse_monitoring(pin):
    pi = pigpio.pi()
    if not pi.connected:
        exit()

    pi.set_mode(pin, pigpio.INPUT)
    pi.set_pull_up_down(pin, pigpio.PUD_UP)

    cb1 = pi.callback(pin, pigpio.FALLING_EDGE, callback)


def print_pulse(pulse, previous_pulse):
    g, l, t, time = pulse
    pg, pl, pt, time = previous_pulse

    if pt == 0:
        return

    ticks_since_callback = t - pt

    average_watts = (1 * 60 * 60 * 1000 * 1000) / ticks_since_callback
    time_period = datetime.timedelta(microseconds=ticks_since_callback)

    print("Average power at source " + str(g) + " = " + str(average_watts) + "W over " + str(time_period.total_seconds()) + "s")


def store_pulse(pulse, db_conn):
    g, l, t, time = pulse
    cur = db_conn.cursor()
    cur.execute("INSERT INTO pulse VALUES(?, ?)", (g, time))
    cur.close()
    db_conn.commit()


def db_thread():
    db_conn = create_connection()
    while True:
        pulse_event.acquire()
        pulse_event.wait()

        pulses_to_handle = []
        last_handled_pulse = (0, 0, 0, None)
        for index in range(len(pulses) - 1, -1, -1):
            if not pulses[index]["handled"]:
                pulses_to_handle.insert(0, pulses[index]["values"])
                pulses[index]["handled"] = True
            else:
                last_handled_pulse = pulses[index]["values"]
                break

        pulse_event.release()

        for pulse in pulses_to_handle:
            store_pulse(pulse, db_conn)
            print_pulse(pulse, last_handled_pulse)
            last_handled_pulse = pulse


def create_connection():
    return mariadb.connect(host='localhost', database='power')


def setup_database():
    con = create_connection()

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS `pulse` (
                      `source`      int(11) NOT NULL,
                      `timestamp`   datetime(6) NOT NULL,
                      PRIMARY KEY (`source`,`timestamp`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)

    cur.close()
    con.commit()

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS `pulse_source` (
                      `source`      int(11) NOT NULL,
                      `description` varchar(100) DEFAULT NULL,
                      PRIMARY KEY (`source`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)
    cur.close()
    con.commit()

    cur = con.cursor()
    cur.execute("""REPLACE 
                      INTO pulse_source 
                    VALUES 
                           (23, "House and Pool"),
                           (24, "Heat Pump")
                    """)
    cur.close()
    con.commit()

    con.close()


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='Monitor pulses on RPi GPIO pin and store into SQLite.')
    parser.add_argument("-p" "--pin", dest="pin", default="23", type=int,
                        help="GPIO port to monitor pulses on")

    args = parser.parse_args()

    print("Setting up the database ...")
    setup_database()

    print("Setting up the database update thread ...")
    db_thread = Thread(target=db_thread)
    db_thread.start()

    print("Setting up the pulse monitoring on pin {}".format(str(args.pin)))
    setup_pulse_monitoring(args.pin)

    print("Monitoring pulses ... ")

    db_thread.join()

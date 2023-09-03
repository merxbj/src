import os
import argparse
import logging
from logging.handlers import RotatingFileHandler
import sys
from pathlib import Path

# hardware access
import pigpio

# date & time stuff
from datetime import datetime, timedelta
from dateutil import tz

# threading
from threading import Thread
from threading import Condition

# database
import mariadb

pulses = []
pulse_event = Condition()


def get_log_path():
    return os.path.join(str(Path.home()), "log/power/")


def callback(gpio, level, tick):
    if len(pulses) >= 552000:
        del pulses[0]

    pulse_event.acquire()
    pulses.append({"values": (gpio, level, tick, datetime.utcnow().replace(tzinfo=tz.tzutc())), "handled": False})
    pulse_event.notify()
    pulse_event.release()


def setup_pulse_monitoring(pin):
    pi = pigpio.pi()
    if not pi.connected:
        exit()

    pi.set_mode(pin, pigpio.INPUT)
    pi.set_pull_up_down(pin, pigpio.PUD_UP)

    pi.callback(pin, pigpio.FALLING_EDGE, callback)


def log_pulse(pulse, previous_pulse):
    g, l, t, time = pulse
    pg, pl, pt, time = previous_pulse

    if pt == 0:
        return

    ticks_since_callback = t - pt

    average_watts = (1 * 60 * 60 * 1000 * 1000) / ticks_since_callback
    time_period = timedelta(microseconds=ticks_since_callback)

    logging.info("Average power at source {} = {:.2f}W over {:.3f}s".format(
        g, average_watts, time_period.total_seconds()))


def store_pulse(pulse, db_conn):
    g, l, t, time = pulse
    cur = db_conn.cursor()
    cur.execute("INSERT INTO pulse VALUES(?, ?)", (g, time))
    cur.close()
    db_conn.commit()


def register_available_date(pulse, db_conn):
    g, l, t, time = pulse
    cur = db_conn.cursor()
    cur.execute("REPLACE INTO available_date VALUES(?, ?)", (g, time.date()))
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
            register_available_date(pulse, db_conn)
            log_pulse(pulse, last_handled_pulse)
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

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS `available_date` (
                      `source`          int(11) NOT NULL,
                      `available_date`   date NOT NULL,
                      PRIMARY KEY (`source`, `available_date`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)
    cur.close()
    con.commit()

    cur = con.cursor()
    cur.execute("""REPLACE 
                      INTO pulse_source 
                    VALUES 
                           (21, "House and Pool"),
                           (16, "Heat Pump")
                    """)
    cur.close()
    con.commit()

    con.close()


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='Monitor pulses on RPi GPIO pin and store into SQLite.')
    parser.add_argument("-p" "--pin", dest="pin", default="23", type=int,
                        help="GPIO port to monitor pulses on")

    args = parser.parse_args()

    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_log_path(), "power_{}.log".format(args.pin)), encoding="utf-8",
                            maxBytes=10485760, backupCount=20),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.DEBUG,
        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")

    logging.info("Setting up the database ...")
    setup_database()

    logging.info("Setting up the database update thread ...")
    db_thread = Thread(target=db_thread)
    db_thread.start()

    logging.info("Setting up the pulse monitoring on pin {}".format(str(args.pin)))
    setup_pulse_monitoring(args.pin)

    logging.info("Monitoring pulses ... ")

    db_thread.join()

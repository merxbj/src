import datetime
import os
from dateutil import tz
import pigpio
from threading import Thread
from threading import Condition
import sqlite3
from pathlib import Path

pulses = []
pulse_event = Condition()


def callback(g, l, t):
    if len(pulses) >= 552000:
        del pulses[0]

    pulse_event.acquire()
    pulses.append({"values": (g, l, t, datetime.datetime.utcnow().replace(tzinfo=tz.tzutc())), "handled": False})
    pulse_event.notify()
    pulse_event.release()


def setup_pulse_monitoring():
    pi = pigpio.pi()
    if not pi.connected:
        exit()

    pin = 23

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
    params = {
        "source": g,
        "timestamp": time.isoformat()
    }
    cur = db_conn.cursor()
    cur.execute("INSERT INTO pulse VALUES(:source, :timestamp)", params)
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


def get_data_path():
    return os.path.join(str(Path.home()), "data/power/")


def create_connection():
    return sqlite3.connect(os.path.join(get_data_path(), "power.dat"), timeout=120)


def setup_database():
    data_path = get_data_path()
    if not os.path.exists(data_path):
        os.makedirs(data_path)

    con = create_connection()

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS pulse (
                        source      INTEGER,
                        timestamp   TEXT,
                        PRIMARY KEY (source, timestamp)
                       );
                """)

    cur.close()
    con.commit()
    con.close()


if __name__ == '__main__':
    print("Setting up the database ...")
    setup_database()

    print("Setting up the database update thread ...")
    db_thread = Thread(target=db_thread)
    db_thread.start()

    print("Setting up the pulse monitoring")
    setup_pulse_monitoring()

    print("Monitoring pulses ... ")

    db_thread.join()

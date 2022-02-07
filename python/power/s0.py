import datetime
import os

import pigpio
from threading import Thread
from threading import Condition
import sqlite3

ticks = []
tick_event = Condition()


def callback(g, l, t):
    if len(ticks) >= 552000:
        del ticks[0]

    tick_event.acquire()
    ticks.append({"values": (g, l, t, datetime.datetime.utcnow()), "handled": False})
    tick_event.notify()
    tick_event.release()


def setup_pulse_monitoring():
    pi = pigpio.pi()
    if not pi.connected:
        exit()

    pin = 23

    pi.set_mode(pin, pigpio.INPUT)
    pi.set_pull_up_down(pin, pigpio.PUD_UP)

    cb1 = pi.callback(pin, pigpio.FALLING_EDGE, callback)


def print_tick(tick, previous_tick):

    g, l, t, time = tick
    pg, pl, pt, time = previous_tick

    if pt == 0:
        return

    ticks_since_callback = t - pt

    average_watts = (1 * 60 * 60 * 1000 * 1000) / ticks_since_callback
    time_period = datetime.timedelta(microseconds=ticks_since_callback)

    print("Average watts = " + str(average_watts) + " over " + str(time_period.total_seconds()) + "s")


def store_tick(tick, db_conn):
    g, l, t, time = tick
    params = {
        "year": time.year,
        "month": time.month,
        "day": time.day,
        "hour": time.hour,
        "minute": time.minute,
        "second": time.second,
        "millis": int(time.microsecond / 1000)
    }
    with db_conn.cursor() as cur:
        cur.execute("INSERT INTO ticks VALUES(:year, :month, :day, :hour, :minute, :second, :millis)", params)
        db_conn.commit()


def db_thread(db_conn):
    while True:
        tick_event.acquire()
        tick_event.wait()

        ticks_to_handle = []
        last_handled_tick = (0, 0, 0)
        for index in range(len(ticks) - 1, -1, -1):
            if not ticks[index]["handled"]:
                ticks_to_handle.insert(0, ticks[index]["values"])
                ticks[index]["handled"] = True
            else:
                last_handled_tick = ticks[index]["values"]
                break

        tick_event.release()

        for tick in ticks_to_handle:
            store_tick(tick, db_conn)
            print_tick(tick, last_handled_tick)
            last_handled_tick = tick


def setup_database():
    data_path = "~/data/power".format(**os.environ)
    if not os.path.exists(data_path):
        os.makedirs(data_path)

    con = sqlite3.connect(os.path.join(data_path, "power.dat"))

    with con.cursor() as cur:
        cur.execute("""CREATE TABLE IF NOT EXISTS ticks (
                            year    INTEGER,
                            month   INTEGER,
                            day     INTEGER,
                            hour    INTEGER,
                            minute  INTEGER,
                            second  INTEGER,
                            millis  INTEGER,
                            PRIMARY KEY (year, month, day, hour, minute, second, millis)
                           );
            """)
        con.commit()

    return con


if __name__ == '__main__':

    connection = setup_database()

    db_thread = Thread(target=db_thread(connection))
    db_thread.start()

    setup_pulse_monitoring()
    print("Reading impulses ... ")

    db_thread.join()

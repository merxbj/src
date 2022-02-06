import datetime

import pigpio
from threading import Thread
from threading import Condition

ticks = []
tick_event = Condition()


def callback(g, l, t):
    if len(ticks) >= 552000:
        del ticks[0]

    tick_event.acquire()
    ticks.append({"values": (g, l, t), "handled": False})
    tick_event.set()
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

    g, l, t = tick
    pg, pl, pt = previous_tick

    if pt == 0:
        return

    ticks_since_callback = t - pt

    average_watts = (1 * 60 * 60 * 1000 * 1000) / ticks_since_callback
    time_period = datetime.timedelta(microseconds=ticks_since_callback)

    print("Average watts = " + str(average_watts) + " over " + str(time_period.total_seconds()) + "s")


def db_thread():
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

        tick_event.clear()
        tick_event.release()

        for tick in ticks_to_handle:
            print_tick(tick, last_handled_tick)
            last_handled_tick = tick


if __name__ == '__main__':

    db_thread = Thread(target=db_thread)
    db_thread.start()

    setup_pulse_monitoring()
    print("Reading impulses ... ")

    db_thread.join()

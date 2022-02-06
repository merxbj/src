import datetime
import pigpio
import time

last_tick = 0


def callback(g, l, t):
    global last_tick
    ticks_since_callback = t - last_tick
    last_tick = t

    average_watts = (1 * 60 * 60 * 1000 * 1000) / ticks_since_callback
    time_period = datetime.timedelta(microseconds=ticks_since_callback)

    print("\t\tAverage watts = " + str(average_watts) + " over " + str(time_period.total_seconds()) + "s")


pi = pigpio.pi()
if not pi.connected:
    exit()

last_tick = pi.get_current_tick()

pin = 23

pi.set_mode(pin, pigpio.INPUT)
pi.set_pull_up_down(pin, pigpio.PUD_UP)

cb1 = pi.callback(pin, pigpio.FALLING_EDGE, callback)

while True:
    print("Reading impulses ... ")
    time.sleep(1000)

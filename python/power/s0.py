import datetime
import pigpio
import time

last_tick = 0


def callback(g, l, t):
    global last_tick

    if last_tick == 0:
        last_tick = t
        return

    ticks_since_callback = t - last_tick
    last_tick = t

    average_watts = (1 * 60 * 60 * 1000 * 1000) / ticks_since_callback
    time_period = datetime.timedelta(microseconds=ticks_since_callback)

    print("Average watts = " + str(average_watts) + " over " + str(time_period.total_seconds()) + "s")


def setup_pulse_monitoring():
    pi = pigpio.pi()
    if not pi.connected:
        exit()

    global last_tick

    pin = 23

    pi.set_mode(pin, pigpio.INPUT)
    pi.set_pull_up_down(pin, pigpio.PUD_UP)

    cb1 = pi.callback(pin, pigpio.FALLING_EDGE, callback)


if __name__ == '__main__':

    setup_pulse_monitoring()
    print("Reading impulses ... ")

    while True:
        time.sleep(1000)

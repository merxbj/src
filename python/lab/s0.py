import pigpio, time


def callback(g, l, t):
    print("Callback " + str(l) + " " + str(t))


pi = pigpio.pi()
if not pi.connected:
    exit()

pin = 23

pi.set_mode(pin, pigpio.INPUT)
pi.set_pull_up_down(pin, pigpio.PUD_UP)

cb1 = pi.callback(pin, pigpio.FALLING_EDGE, callback)

while True:
    print("Direct " + str(pi.read(pin)) + " " + str(pi.get_current_tick()))
    time.sleep(1)

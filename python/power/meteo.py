import websocket
import json
import requests
import base64
import argparse

sensor = 0


def decode_fixed_decimal(hex_encoded):
    number = int(hex_encoded, base=16)
    base = number & 0x7FF                  # bits 0 - 10 are the base
    scale = number >> 11 & 0xF             # bits 11 - 14 are scaling factor
    if 0x8000 & number:                    # bit 15 is sign
        base = -((0x7FF & ~number) + 1)    # negative numbers are 1's complement
    value = .01 * base * (1 << scale)      # base is divided by 100 and further divided by the scaling factor
    return value


def on_message(wsapp, message):
    j = json.loads(message)
    if j['dstraw'] == sensor:
        print(str(decode_fixed_decimal(j['datahex'])))


def get_initial_values(host, user_name, password):
    x = (user_name + ":" + password)
    header = {'Authorization': "Basic " + base64.b64encode(x.encode()).decode()}
    r = requests.post(f"http://{host}/apps/localbus.lp?", headers=header)
    return r.content


def get_sensor_value(data):
    for obj in data['objects']:
        if obj['id'] == sensor:
            print(str(obj))
            return decode_fixed_decimal(obj['datahex'])


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Monitor meteorological data.')
    parser.add_argument("-s" "--sensor", dest="sensor", default="12289", type=int,
                        help="Sensor to pull data from")
    parser.add_argument("-u" "--user", dest="user", type=str,
                        help="User name to connect to the meteorological station")
    parser.add_argument("-p" "--password", dest="password", type=str,
                        help="Password to connect to the meteorological station")
    parser.add_argument("--host", dest="host", type=str,
                        help="hostname[:port] of the meteorological station")

    args = parser.parse_args()

    sensor = args.sensor

    data = json.loads(get_initial_values(args.host, args.user, args.password))
    print(get_sensor_value(data))

    auth = data["auth"]
    wsapp = websocket.WebSocketApp(f"ws://{args.host}/apps/localbus.lp?auth={auth}", on_message=on_message)
    wsapp.run_forever()

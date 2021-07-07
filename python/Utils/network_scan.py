import socket

network_ip = "192.168.88."
port = 80
timeout = 1


def is_open(ip):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.settimeout(timeout)
        try:
            s.connect((ip, int(port)))
            s.shutdown(socket.SHUT_RDWR)
            return True
        except:
            return False
        finally:
            s.close()


for host in range(1,255):
    ip_to_check = network_ip + str(host)
    print("Checking " + ip_to_check + " ... ", end="")
    if is_open(ip_to_check):
        print("UP")
    else:
        print("DOWN")
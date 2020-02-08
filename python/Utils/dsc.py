import sys


class Driver:
    def __init__(self, pubname, provider, cname, version):
        self.pubname = str.split(pubname, ':', 2)[1].strip()
        self.provider = str.split(provider, ':', 2)[1].strip()
        self.cname = str.split(cname, ':', 2)[1].strip()
        self.version = str.split(version, ':', 2)[1].strip()

    def __str__(self):
        return self.pubname + '\t' + self.provider + '\t' + self.cname + '\t' + self.version


def parse_driver_info(lines):
    driver = Driver(lines[0], lines[2], lines[3], lines[5])
    return driver


def main(argv):
    with open(r"c:\temp\driveroutput.txt", 'r') as f:
        content = f.readlines()
    content = [x.strip() for x in content]

    drivers = []

    for i in range(0, len(content)):
        if content[i].startswith('Published Name'):
            drivers.append(parse_driver_info(content[i:i + 6]))
            i += 6

    for driver in drivers:
        if driver.cname == 'Grafické adaptéry' and driver.provider == 'NVIDIA':
            if '25.21.14.1967' not in driver.version:
                print('pnputil /delete-driver ' + driver.pubname)


if __name__ == '__main__':
    main(sys.argv)

import os
import sys
from os import path

__author__ = 'merxbj'


def main(argv):
    dry = '--dry' in argv
    for root, dirs, files in os.walk(argv[1]):
        jpgs = [jpg for jpg in files if jpg.lower().endswith('jpg')]
        raws = [raw.lower() for raw in files if raw.lower().endswith('cr2')]
        for jpg in jpgs:
            if jpg.lower().replace('.jpg', '.cr2') in raws:
                jpg_path = path.join(root, jpg)
                if not dry:
                    os.remove(jpg_path)
                print('Removed {}'.format(jpg_path))


if __name__ == '__main__':
    main(sys.argv)
from os import listdir, walk
from os.path import isfile, basename, expanduser, join
from datetime import datetime
import sys
import re
from generator import WeeklyReportGenerator
from printers import ConsolePrinter


def guess_last_report(downloads_path):
    regex = re.compile(r"(?P<from>\d{4}-\d{2}-\d{2})-(?P<to>\d{4}-\d{2}-\d{2}) details(\s\((?P<order>\d+)\))*.csv")
    candidates = []
    for (dir_path, dir_names, file_names) in walk(expanduser(downloads_path)):
        candidates.extend(file_names)
        break

    report_files = []
    for candidate in candidates:
        parts = regex.match(basename(candidate))
        if parts:
            parts = parts.groupdict()
            fr = datetime.strptime(parts["from"], "%Y-%m-%d")
            if parts["order"] is not None:
                order = int(parts["order"])
            else:
                order = 0
            report_files.append((fr, order, candidate))

    if len(report_files) == 0:
        raise Exception("No candidate report files found :-(")
    else:
        print("Found the following report files:\n\t", [rf[2] for rf in report_files])

    return join(downloads_path, sorted(report_files, key=lambda rf: (rf[0], rf[1]), reverse=True)[0][2])


def main():

    if (len(sys.argv) == 2) and (isfile(sys.argv[1])):
        file_path = sys.argv[1]
    elif len(sys.argv) == 2:
        file_path = guess_last_report(sys.argv[1])
    else:
        #debugging
        file_path = r"~/Downloads/2014-04-21-2014-04-2 details.csv"

    report = WeeklyReportGenerator().generate(file_path)
    ConsolePrinter(report).print_report()


if __name__ == "__main__":
    main()
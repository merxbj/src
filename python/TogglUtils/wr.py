from os import walk
import os
from os.path import isfile, basename, expanduser, join
from datetime import datetime, timedelta
import sys
import re

from generator import WeeklyReportGenerator
from toggl.printer.html import HtmlPrinter
from toggl.datasource.csv import CsvReportParser
from toggl.datasource.web import ReportApi


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


def week_magic(day):
    day_of_week = day.weekday()

    to_beginning_of_week = timedelta(days=day_of_week)
    beginning_of_week = day - to_beginning_of_week

    to_end_of_week = timedelta(days=6 - day_of_week)
    end_of_week = day + to_end_of_week

    return beginning_of_week, end_of_week


def main():
    if len(sys.argv) < 2:
        raise Exception('Unexpected number of arguments: {0}!'.format(len(sys.argv)))

    #csv ~/Downloads
    if sys.argv[1] == 'csv':
        if (len(sys.argv) == 3) and (isfile(sys.argv[2])):
            file_path = sys.argv[2]
        elif len(sys.argv) == 3:
            file_path = guess_last_report(sys.argv[2])
        else:
            file_path = r"~/Downloads/2014-04-21-2014-04-2 details.csv"

        data_source = CsvReportParser(file_path)

    elif sys.argv[1] == 'web':
        if len(sys.argv) >= 3 and sys.argv[2] == 'last':
            since, until = week_magic(datetime.now() - timedelta(days=7))
            clients = sys.argv[3:]
        elif len(sys.argv) >= 3 and sys.argv[2] == 'this':
            since, until = week_magic(datetime.now())
            clients = sys.argv[3:]
        else:
            since, until = week_magic(datetime.now())
            clients = sys.argv[2:]

        data_source = ReportApi(since, until, clients)

    else:
        raise Exception('Unexpected data source: {0}'.format(sys.argv[1]))

    report = WeeklyReportGenerator().generate(data_source)
    HtmlPrinter(report, os.path.expanduser('~/Desktop/Weekly_Report.html')).print_report()


if __name__ == "__main__":
    main()
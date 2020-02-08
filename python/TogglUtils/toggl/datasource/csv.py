import csv
from datetime import timedelta, datetime
import re
from os import path

__author__ = 'merxbj'


def parse_duration(string):
    regex = re.compile(r"(?P<hours>\d{2}):(?P<minutes>\d{2}):(?P<seconds>\d{2})")
    parts = regex.match(string)
    if not parts:
        raise NameError("Invalid duration format")

    parts = parts.groupdict()
    time_params = {}
    for (name, param) in parts.items():
        if param:
            time_params[name] = int(param)
    return timedelta(**time_params).total_seconds() * 1000


class CsvReportParser:
    def __init__(self, detailed_report_path):
        self.detailed_report_path = detailed_report_path

    @staticmethod
    def process_row(row):
        return {'project': row['Project'],
                'task': row['Task'],
                'client': row['Client'],
                'start': datetime.strptime(row['Start date'] + "T" + row['Start time'], "%Y-%m-%dT%H:%M:%S"),
                'end': datetime.strptime(row['End date'] + "T" + row['End time'], "%Y-%m-%dT%H:%M:%S"),
                'dur': parse_duration(row['Duration'])}

    def get_detailed_report(self):
        file_path = path.expanduser(self.detailed_report_path)
        print("Will process " + file_path)

        entries = []
        with open(file_path, 'r+', encoding="utf-8-sig") as file:
            entry_reader = csv.DictReader(file)
            for row in entry_reader:
                entries.append(self.process_row(row))

        return entries


def main():
    pass


if __name__ == '__main__':
    main()
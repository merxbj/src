import csv
import sys
import re
import math
from datetime import datetime, timedelta

CALCULATED_TO_PROVIDED_DURATION_RATIO_MAX = 0.05

class ReportColumn:
    Project = "Project"
    Task = "Task"
    StartDate = "Start date"
    StartTime = "Start time"
    EndDate = "End date"
    EndTime = "End time"
    Duration = "Duration"

def check_duration(calculated, provided, task):
    ratio = calculated / provided
    ratio = ratio if ratio < 1.0 else ratio - 1
    if ratio > CALCULATED_TO_PROVIDED_DURATION_RATIO_MAX:
        print("WARNING! Provided duration {0} does not fit within a reasonable ratio with\
              calculated duration {1} for {2}!".format(provided, calculated, task))

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
    return timedelta(**time_params)

def process_row(row, report):

    task = row[ReportColumn.Project] + "/" + row[ReportColumn.Task]

    st = datetime.strptime(row[ReportColumn.StartDate]+"T"+row[ReportColumn.StartTime], "%Y-%m-%dT%H:%M:%S")
    et = datetime.strptime(row[ReportColumn.EndDate]+"T"+row[ReportColumn.EndTime], "%Y-%m-%dT%H:%M:%S")

    calculated_diff = et-st
    provided_diff = parse_duration(row[ReportColumn.Duration])
    check_duration(calculated_diff, provided_diff, task)

    if task in report:
        taskreport = report[task]
    else:
        taskreport = {}
        report[task] = taskreport

    if st.weekday() not in taskreport:
        taskreport[st.weekday()] = calculated_diff
    else:
        taskreport[st.weekday()] = taskreport[st.weekday()] + calculated_diff

def print_report(report):
    dow = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]
    total = timedelta()
    for task, taskreport in report.items():
        for day, duration in taskreport.items():
            print("{0} - {1} - {2}".format(task, dow[day], duration))
            total += duration
    print("Total = {0:2}:{1:2}:{2:2}".format(int(total.total_seconds()) // 3600, (int(total.total_seconds()) % 3600) // 60, (int(total.total_seconds()) % 3600) % 60))

def main():

    if len(sys.argv) == 2:
        path = sys.argv[1]
    else:
        path = r"C:\Users\JM185267\Downloads\2014-04-21-2014-04-27 details (1).csv"

    print("Will process " + path)

    report = {}
    with open(path, 'r+', encoding="utf-8-sig") as file:
        entry_reader = csv.DictReader(file)
        for row in entry_reader:
            process_row(row, report)

    print_report(report)


if __name__ == "__main__":
    main()

import csv
import re
from datetime import datetime, timedelta
from os import path


class ReportColumn:
    Project = "Project"
    Task = "Task"
    StartDate = "Start date"
    StartTime = "Start time"
    EndDate = "End date"
    EndTime = "End time"
    Duration = "Duration"


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


class WeeklyReportGenerator:
    _report = {}
    _CALCULATED_TO_PROVIDED_DURATION_RATIO_MAX = 0.05

    def check_duration(self, calculated, provided, task):
        ratio = calculated / provided
        ratio = ratio if ratio < 1.0 else ratio - 1
        if ratio > self._CALCULATED_TO_PROVIDED_DURATION_RATIO_MAX:
            print("WARNING! Provided duration {0} does not fit within a reasonable ratio with\
                  calculated duration {1} for {2}!".format(provided, calculated, task))

    def process_row(self, row):
        task = row[ReportColumn.Project] + "/" + row[ReportColumn.Task]

        st = datetime.strptime(row[ReportColumn.StartDate] + "T" + row[ReportColumn.StartTime], "%Y-%m-%dT%H:%M:%S")
        et = datetime.strptime(row[ReportColumn.EndDate] + "T" + row[ReportColumn.EndTime], "%Y-%m-%dT%H:%M:%S")

        calculated_diff = et - st
        provided_diff = parse_duration(row[ReportColumn.Duration])
        self.check_duration(calculated_diff, provided_diff, task)

        if task in self._report:
            task_report = self._report[task]
        else:
            task_report = {}
            self._report[task] = task_report

        if st.weekday() not in task_report:
            task_report[st.weekday()] = calculated_diff
        else:
            task_report[st.weekday()] = task_report[st.weekday()] + calculated_diff

    def generate(self, detailed_report_path):
        file_path = path.expanduser(detailed_report_path)
        print("Will process " + file_path)

        with open(file_path, 'r+', encoding="utf-8-sig") as file:
            entry_reader = csv.DictReader(file)
            for row in entry_reader:
                self.process_row(row)

        return self._report

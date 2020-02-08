from datetime import timedelta
from toggl.printer.util import format_duration, DOW


class ConsolePrinter:
    def __init__(self, report):
        self._report = report

    def print_report(self):
        total = timedelta()

        print("\n")

        for task, task_report in self._report.items():
            print("{0}:".format(task))
            for day, duration in task_report.items():
                print("\t{0:10}: {1:8}".format(DOW[day], str(duration)))
                total += duration
            print("")

        print('Total =', format_duration(total))

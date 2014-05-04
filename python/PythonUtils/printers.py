from datetime import timedelta


class ConsolePrinter:
    def __init__(self, report):
        self._report = report

    def print_report(self):
            dow = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]
            total = timedelta()
            for task, task_report in self._report.items():
                for day, duration in task_report.items():
                    print("{0} - {1} - {2}".format(task, dow[day], duration))
                    total += duration
            print("Total = {0:2}:{1:2}:{2:2}".format(int(total.total_seconds()) // 3600,
                                                     (int(total.total_seconds()) % 3600) // 60,
                                                     (int(total.total_seconds()) % 3600) % 60))
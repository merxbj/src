from datetime import timedelta


class ConsolePrinter:
    def __init__(self, report):
        self._report = report

    def print_report(self):
            dow = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]
            total = timedelta()

            print("\n")

            for task, task_report in self._report.items():
                print("{0}:".format(task))
                for day, duration in task_report.items():
                    print("\t{0:10}: {1:8}".format(dow[day], str(duration)))
                    total += duration
                print("")


            print("Total = {0:02}:{1:02}:{2:02}".format(int(total.total_seconds()) // 3600,
                                                     (int(total.total_seconds()) % 3600) // 60,
                                                     (int(total.total_seconds()) % 3600) % 60))
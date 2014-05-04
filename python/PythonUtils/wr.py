import sys
from generator import WeeklyReportGenerator
from printers import ConsolePrinter


def main():

    if len(sys.argv) == 2:
        file_path = sys.argv[1]
    else:
        file_path = r"~/Downloads/2014-04-21-2014-04-27 details.csv"

    report = WeeklyReportGenerator().generate(file_path)
    ConsolePrinter(report).print_report()


if __name__ == "__main__":
    main()
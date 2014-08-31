from datetime import timedelta
from os import linesep
from toggl.printer.util import format_duration
from html import escape

__author__ = 'merxbj'


class HtmlPrinter:
    def __init__(self, report):
        self._report = report

    def print_report(self):
        style = r'table,th,td{border:1px solid black;border-collapse:collapse;}th,td{padding:5px;} ' \
                r'ul {list-style: none;}'
        page = '<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">' \
               '<head><title>Week Report</title>' \
               '<style type="text/css">{0}</style></head><body>{1}</body></html>'.format(style, self.report_to_html())
        print(page)

    def report_to_html(self):
        day_totals = {}
        task_details = {}

        report = []

        report_header = '<h1>Week Report</h1>'
        report.append(report_header)

        table = '<table>' \
                '   <tr>' \
                '       <th>Task</th>' \
                '       <th>Monday</th>' \
                '       <th>Tuesday</th>' \
                '       <th>Wednesday</th>' \
                '       <th>Thursday</th>' \
                '       <th>Friday</th>' \
                '       <th>Saturday</th>' \
                '       <th>Sunday</th>' \
                '       <th>Total</th>' \
                '   </tr>'
        for task, task_report in self._report.items():

            needs_details = False
            if 'ELB' in task.upper():
                task_details[task] = set()
                needs_details = True

            # open the row and add task name

            table += '<tr>'
            table += '<td><strong>{}</strong></td>'.format(escape(task))

            task_total = timedelta()
            for day in range(7):

                # find the time spent on this task at this day
                if day in task_report:
                    duration = task_report[day]['duration']
                    table += '<td>{}</td>'.format(duration)
                    if needs_details and task_report[day]['descriptions']:
                        task_details[task] |= task_report[day]['descriptions']
                else:
                    duration = timedelta(0)
                    table += '<td></td>'

                # print task/day data

                # increment totals
                task_total += duration
                if day not in day_totals:
                    day_totals[day] = timedelta()
                day_totals[day] += duration

            # add task total
            table += '<td><strong>{}</strong></td>'.format(format_duration(task_total))

            # close the row
            table += '</tr>'

        # add day and grand totals
        grand_total = timedelta()
        table += '<tr><td><strong>Total:</strong></td>'
        for day in range(7):
            table += '<td><strong>{}</strong></td>'.format(format_duration(day_totals[day]))
            grand_total += day_totals[day]
        table += '<td><strong>{}</strong></td></tr>'.format(format_duration(grand_total))

        # we are done
        table += '</table>'
        report.append(table)

        footer = '<h1>Task Details</h1>'
        for task, details in task_details.items():
            footer += '<h2>{}</h2>'.format(task)
            footer += self.format_details(details)
        report.append(footer)

        return ''.join(report)

    @staticmethod
    def format_details(details):
        task_details = '<ul>'
        for detail in details:
            task_details += '<li>{0}</li>'.format(detail)
        task_details += '</ul>'
        return task_details
from datetime import timedelta
from toggl.printer.util import format_duration

__author__ = 'merxbj'


class HtmlPrinter:
    def __init__(self, report):
        self._report = report

    def print_report(self):
        style = r'table,th,td{border:1px solid black;border-collapse:collapse;}th,td{padding:5px;}'
        page = '<html><head><h1>Week Report<h1></head><style>{0}</style><body>{1}</body></html>'.format(style,
                                                                                                        self.report_to_html())
        print(page)

    def report_to_html(self):
        day_totals = {}
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

            # open the row and add task name

            table += '<tr>'
            table += '<td><strong>{}<strong></td>'.format(task)

            task_total = timedelta()
            for day in range(7):

                # find the time spent on this task at this day
                if day in task_report:
                    duration = task_report[day]
                    table += '<td>{}</td>'.format(duration)
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
        table += '<td><strong>{}</strong></td>'.format(format_duration(grand_total))

        # we are done
        table += '</table>'

        return table
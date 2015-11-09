from datetime import timedelta


class WeeklyReportGenerator:
    def __init__(self):
        pass

    _report = {}
    _CALCULATED_TO_PROVIDED_DURATION_RATIO_MAX = 0.05

    def check_duration(self, calculated, provided, task):
        # add 1ms to provided duration to prevent divisions by 0
        ratio = calculated / (provided + timedelta(milliseconds=1))
        ratio = 1 - ratio if ratio < 1.0 else ratio - 1
        if ratio > self._CALCULATED_TO_PROVIDED_DURATION_RATIO_MAX:
            print("WARNING! Provided duration {0} does not fit within a reasonable ratio with\
                  calculated duration {1} for {2}!".format(provided, calculated, task))

    def process_entry(self, entry):
        task = entry['project'] + " : " + entry['task']
        st = entry['start']
        et = entry['end']

        calculated_diff = et - st
        provided_diff = timedelta(milliseconds=entry['dur'])
        self.check_duration(calculated_diff, provided_diff, task)

        if task in self._report:
            task_report = self._report[task]
        else:
            task_report = {}
            self._report[task] = task_report

        if st.weekday() not in task_report:
            task_report[st.weekday()] = {'duration': calculated_diff, 'descriptions': {entry['description']}}
        else:
            task_report[st.weekday()]['duration'] = task_report[st.weekday()]['duration'] + calculated_diff
            task_report[st.weekday()]['descriptions'].add(entry['description'])

    def generate(self, data_source):
        entries = data_source.get_detailed_report()
        for entry in entries:
            self.process_entry(entry)

        return self._report

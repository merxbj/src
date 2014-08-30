import requests
import math
from datetime import datetime


class ReportApi:
    def __init__(self, since, until, clients):
        self.since = since
        self.until = until
        self.clients = clients

    def get_report_page(self, page=1):
        headers = {'content-type': 'application/json'}
        params = {'workspace_id': 288359,
                  'since': datetime.isoformat(self.since),
                  'until': datetime.isoformat(self.until),
                  'user_agent': 'merxbj_api'}

        if page > 1:
            params['page'] = page

        response = requests.get('https://toggl.com/reports/api/v2/details',
                                auth=('84a975312e9cf2f028037f5f292133ce', 'api_token'),
                                headers=headers,
                                params=params)
        return response.json()

    def get_detailed_report(self):

        entries = []

        page = 1
        while True:
            report = self.get_report_page(page)
            for entry in report['data']:
                if len(self.clients) == 0 or entry['client'] in self.clients:
                    entries.append(self.parse_entry(entry))

            entry_count = report['total_count']
            per_page = report['per_page']

            page_count = int(math.ceil(entry_count / per_page))
            if page == page_count:
                break

            page += 1

        return entries

    @staticmethod
    def parse_entry(entry):
        entry['start'] = datetime.strptime(entry['start'][:19], '%Y-%m-%dT%H:%M:%S')
        entry['end'] = datetime.strptime(entry['end'][:19], '%Y-%m-%dT%H:%M:%S')
        entry['task'] = entry['task'] if not entry['task'] is None else entry['description']
        return entry


def main():
    reports = ReportApi(datetime(2014, 4, 1), datetime(2014, 4, 30), ['Integri'])
    report = reports.get_detailed_report()
    for entry in report:
        print(entry['client'], entry['project'], entry['task'], entry['description'], entry['start'], entry['end'])


if __name__ == '__main__':
    main()






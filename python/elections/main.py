from xml.dom import minidom
import os


def get_election_data():

    election_data = {}
    path = os.path.expanduser('~/Downloads/vysledky.xml')
    with open(path, encoding='utf-8') as results:
        xml_dom = minidom.parse(results)
        results_cr = xml_dom.getElementsByTagName('CR')[0].getElementsByTagName('UCAST')
        election_data[0] = {}
        election_data[0]['total_valid_votes'] = int(results_cr[0].attributes['PLATNE_HLASY'].value)

        results_regions = xml_dom.getElementsByTagName('KRAJ')
        for result_region in results_regions:
            region_num = int(result_region.attributes['CIS_KRAJ'].value)
            election_data[region_num] = {}
            election_data[region_num]['name'] = result_region.attributes['NAZ_KRAJ'].value
            election_data[region_num]['total_valid_votes'] = int(result_region.getElementsByTagName('UCAST')[0].attributes['PLATNE_HLASY'].value)

    return election_data


if __name__ == '__main__':
    data = get_election_data()

    total = 0
    for region_id, region_data in data.items():
        if region_id == 0:
            total = region_data['total_valid_votes']
        else:
            total -= region_data['total_valid_votes']

    print(data)
    print(total)


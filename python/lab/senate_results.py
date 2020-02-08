import datetime
import requests
import time
import os

from xml.dom import minidom
from termcolor import colored

done = False
previous_votes = {}

while not done:
    response = requests.get('https://volby.cz/pls/senat/vysledky?datum_voleb=20181005')

    os.system('cls')
    print('Updated @ {}'.format(datetime.datetime.now().strftime('%c')))
    print('')

    xml_dom = minidom.parseString(response.text)

    for xml_area in xml_dom.getElementsByTagName("OBVOD"):
        area_name = xml_area.attributes['NAZEV'].value

        candidates = []
        for candidate_xml in xml_area.getElementsByTagName("KANDIDAT"):
            id = int(candidate_xml.attributes['PORADOVE_CISLO'].value)
            first_name = candidate_xml.attributes['JMENO'].value
            last_name = candidate_xml.attributes['PRIJMENI'].value
            name = first_name + ' ' + last_name
            votes_perc = float(candidate_xml.attributes['HLASY_PROC_1KOLO'].value)
            votes_count = int(candidate_xml.attributes['HLASY_1KOLO'].value)
            candidates.append((id, name, votes_count, votes_perc))

        candidates.sort(key=lambda p: p[3], reverse=True)
        candidates = candidates[0:2]

        attendance = float(xml_area.getElementsByTagName('UCAST')[0].attributes['OKRSKY_ZPRAC_PROC'].value)
        votes = int(xml_area.getElementsByTagName('UCAST')[0].attributes['PLATNE_HLASY'].value)

        votes_previous = votes if area_name not in previous_votes else previous_votes[area_name]
        previous_votes[area_name] = votes
        color = 'green' if votes_previous != votes else 'white'

        print(colored('{:>20}: {:>6} hlasu, {:>7}% - '.format(area_name, votes, attendance), color), end='')

        for candidate in candidates:
            candidate_color = 'yellow' if candidate[3] > 50.0 else 'white'
            print(colored('{:>20}: {:>7} --> {:>6}%'.format(candidate[1], candidate[2], candidate[3]), candidate_color), end='')
        print('')

    done = 100.0 == float(xml_dom.getElementsByTagName('UCAST')[0].attributes['PLATNE_HLASY_PROC'].value)

    if not done:
        time.sleep(20)

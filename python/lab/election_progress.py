import datetime
from math import ceil
import os
from os import listdir
from os.path import isfile, join
from termcolor import colored

from xml.dom import minidom

previous_mandates = {}
header_written = False

short_names = {'Komunistická strana Čech a Moravy': 'KSČM',
               'Svoboda a přímá demokracie - Tomio Okamura (SPD)': 'SPD',
               'Občanská demokratická strana': 'ODS',
               'Česká strana sociálně demokratická': 'ČSSD',
               'PRO Zdraví a Sport': 'PZS'}

data_path = r"c:\temp\elections\zastup"
res_file_name = 'results.csv'
xml_files = [f for f in listdir(data_path) if isfile(join(data_path, f)) and f.endswith('xml')]
xml_files.sort()

os.remove(join(data_path, res_file_name))

for xml_file in xml_files:

    xml_dom = minidom.parse(open(join(data_path, xml_file), encoding="utf-8"))
    xml_parties = xml_dom.getElementsByTagName("VOLEBNI_STRANA")

    all_party_mandates = {}
    parties = []
    for xml_party in xml_parties:
        name = xml_party.attributes['NAZEV_STRANY'].value
        name = short_names[name] if name in short_names else name
        votes = int(xml_party.attributes['HLASY'].value)
        percent = float(xml_party.attributes['HLASY_PROC'].value)

        party = (name, votes, votes)
        if percent > 5.0:
            parties.append(party)
        if name not in all_party_mandates:
            all_party_mandates[name] = 0

    mandates_max = int(xml_dom.getElementsByTagName('OBEC')[0].attributes['VOLENO_ZASTUP'].value)
    mandates_given = 0
    mandates = {}

    if len(parties) > 0:
        while mandates_given < mandates_max:
            parties.sort(key=lambda p: p[1], reverse=True)
            top = parties.pop(0)

            if top[0] in mandates:
                mandates[top[0]] += 1
            else:
                mandates[top[0]] = 1

            all_party_mandates[top[0]] += 1

            new_top = (top[0], ceil(top[2] / (mandates[top[0]] + 1)), top[2])
            parties.append(new_top)

            mandates_given = mandates_given + 1

        changed = True
        if len(mandates.items()) == len(previous_mandates.items()):
            for key, val in mandates.items():
                if key in previous_mandates:
                    changed = val != previous_mandates[key]
                if changed:
                    break
            for key, val in previous_mandates.items():
                if key in mandates:
                    changed = val != mandates[key]
                if changed:
                    break

    color = 'green' if len(previous_mandates.items()) > 0 and changed else 'white'

    completed = float(xml_dom.getElementsByTagName('UCAST')[0].attributes['OKRSKY_ZPRAC_PROC'].value)
    votes = int(xml_dom.getElementsByTagName('UCAST')[0].attributes['PLATNE_HLASY'].value)
    generated = xml_dom.getElementsByTagName('VYSLEDKY_OBEC')[0].attributes['DATUM_CAS_GENEROVANI'].value

    print('@ ' + generated + ': ' + str(votes) + ' hlasu, ' + str(completed) + '% - ', end='')
    print(colored(mandates, color))

    party_mandates_str = ''
    for key, val in all_party_mandates.items():
        party_mandates_str += ',{}'.format(val)

    with open(join(data_path, res_file_name,), encoding='utf8', mode="a") as res_file:
        if not header_written:
            csv_header = '[date],[total_completed]'
            for key, val in all_party_mandates.items():
                csv_header += ',[{}]'.format(key)
            res_file.write(csv_header + '\n')
            header_written = True
        seconds = datetime.datetime.fromisoformat(generated).timestamp()
        res_file.write('{},{}{}\n'.format(seconds, completed, party_mandates_str))

    previous_mandates = mandates

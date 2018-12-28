from math import ceil

import requests
import time

from xml.dom import minidom

done = False
previous_mandates = {}

short_names = {'Komunistická strana Čech a Moravy':'KSČM',
               'Svoboda a přímá demokracie - Tomio Okamura (SPD)':'SPD',
               'Občanská demokratická strana':'ODS',
               'Česká strana sociálně demokratická':'ČSSD'}

while not done:

    response = requests.get('https://www.volby.cz/pls/kv2018/vysledky_obec?cislo_obce=562971')

    xml_dom = minidom.parseString(response.text)
    xml_parties = xml_dom.getElementsByTagName("VOLEBNI_STRANA")

    parties = []

    for xml_party in xml_parties:
        name = xml_party.attributes['NAZEV_STRANY'].value
        votes = int(xml_party.attributes['HLASY'].value)
        percent = float(xml_party.attributes['HLASY_PROC'].value)
        if percent > 5.0:
            if name in short_names:
                name = short_names[name]
            party = (name, votes, votes)
            parties.append(party)

    mandates_max = int(xml_dom.getElementsByTagName('OBEC')[0].attributes['VOLENO_ZASTUP'].value)
    mandates_given = 0
    mandates = {}

    while mandates_given < mandates_max:
        parties.sort(key=lambda p: p[1], reverse=True)
        top = parties.pop(0)

        if top[0] in mandates:
            mandates[top[0]] += 1
        else:
            mandates[top[0]] = 1

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

    if len(previous_mandates.items()) > 0 and changed:
        print('!!! UPDATE !!! ', end='')

    attendance = float(xml_dom.getElementsByTagName('UCAST')[0].attributes['OKRSKY_ZPRAC_PROC'].value)
    votes = int(xml_dom.getElementsByTagName('UCAST')[0].attributes['PLATNE_HLASY'].value)
    print(str(votes) + ' hlasu, ' + str(attendance) + '% - ', end='')

    print(mandates)

    previous_mandates = mandates
    done = 'false' != xml_dom.getElementsByTagName('OBEC')[0].attributes['JE_SPOCTENO'].value

    if not done:
        time.sleep(20)

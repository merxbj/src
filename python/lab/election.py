import io
import requests
import time
import datetime

forever = True

while forever:

    response = requests.get('https://www.volby.cz/pls/kv2018/vysledky_obec?cislo_obce=562971')

    file_name = 'SnapshotAt' + datetime.datetime.now().strftime("%Y%m%d-%H%M%S") + '.xml'
    file_path = 'c:\\temp\\elections\\zastup\\' + file_name

    print("About to store an elections snapshot into " + file_path)

    with io.open(file_path, "w", encoding="utf-8") as f:
        f.write(response.text)

    print('ElectionDataGatherer goes to sleep...')

    time.sleep(20)

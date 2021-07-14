import os
import xml.etree.ElementTree as et
from argparse import ArgumentParser

parser = ArgumentParser()
parser.add_argument("-d", "--directory", dest="directory",
                    help="Directory with Tran XML(s)", metavar="DIR")
args = parser.parse_args()

for filename in os.listdir(args.directory):
    if filename.startswith("Tran") and filename.endswith(".xml"):
        tree = et.parse(os.path.join(args.directory, filename))
        root = tree.getroot()

        signature_parts = []
        previous_sequence = ""

        # [EventId = '10124' and SubEventId = '258' and Modifier1Id = '1']/Description
        for item in root.findall(".//Detail"):
            event_id = item.find("./EventId")
            subevent_id = item.find("./SubEventId")
            modifier1_id = item.find("./Modifier1Id")
            if event_id.text == "10124" and subevent_id.text == "258" and modifier1_id.text == "1":
                description = item.find("./Description")
                modifier2_id = item.find("./Modifier2Id")
                signature_parts.append({"part": description.text, "index": int(modifier2_id.text)})
            elif event_id.text == "10124" and subevent_id.text == "258" and modifier1_id.text == "2":
                previous_sequence = item.find("./Description").text

        signature_parts.sort(key=lambda part: part["index"])

        signature = ""
        for part in signature_parts:
            signature += part["part"]

        if len(signature) > 0:
            header = root.find("./Header")
            endtime = header.find("EndTime").text
            amount = ""
            sequence = ""

            for nvp in header.findall("./NVPs/NVP"):
                nvp_name = nvp.attrib["name"]
                if nvp_name.startswith("Fiscal.PT.") and nvp_name.endswith(".Amount"):
                    amount = nvp.text
                if nvp_name.startswith("Fiscal.PT.") and nvp_name.endswith(".Sequence"):
                    sequence = nvp.text

            if len(sequence) > 0 and len(endtime) > 0 and len(amount) > 0 and len(previous_sequence) > 0:
                print("({}, {}, {}, {}) -> {}".format(sequence, endtime, amount, previous_sequence, signature))
                print("")

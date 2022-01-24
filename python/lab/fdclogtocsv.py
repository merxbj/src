import io, re, csv, os, json
from argparse import ArgumentParser
from xml.dom import minidom

all_comms = []


def parse_comms_in_file(path):
    print("Processing " + path)

    logs = parse_log(path)
    file_comms = parse_comms(logs)

    all_comms.extend(file_comms)


def parse_comms(logs):
    file_comms = []
    for log in logs:

        comm_xml = "\n".join(log["message"])
        xml_dom = minidom.parseString(comm_xml)

        if log["type"] == "REQUEST IN":
            request = parse_request(xml_dom, log["timestamp"])
            if request is not None:
                file_comms.append({"request": request})
            continue

        if log["type"] == "RESPONSE OUT":
            response = parse_response(xml_dom, log["timestamp"])
            if response:
                assign_response_to_request(file_comms, response)
                continue

    return file_comms


def assign_response_to_request(file_comms, response):
    matching_request_found = False
    for comm in file_comms[::-1]:
        if comm["request"]["Class"] == "ServiceRequest" and comm["request"]["ID"] == response["ID"]:
            comm["response"] = response
            matching_request_found = True
            break
    if not matching_request_found:
        print("Unexpected response found: " + str(response))


def parse_response(comm_xml, timestamp):
    response_xml = comm_xml.getElementsByTagName("ServiceResponse")
    if len(response_xml) == 1:
        errorcodes_xml = response_xml[0].getElementsByTagName("ErrorCode")
        if len(errorcodes_xml) >= 1:
            errorcodes = []
            for errorcode_xml in errorcodes_xml:
                errorcodes.append(get_text(errorcode_xml.childNodes))

            response = {
                "ID": response_xml[0].attributes["RequestID"].value,
                "Result": response_xml[0].attributes["OverallResult"].value,
                "ErrorCode": ",".join(errorcodes),
                "timestamp": timestamp}

            return response

    print("Unexpected response  format:" + comm_xml.toxml())
    return None


def parse_request(comm_xml, timestamp):

    posmessage_xml = comm_xml.getElementsByTagName("POSMessage")
    if len(posmessage_xml) == 1:
        message = {
            "Class": "POSMessage",
            "ID": posmessage_xml[0].attributes["MessageID"].value,
            "Type": posmessage_xml[0].attributes["MessageType"].value,
            "WorkstationID": posmessage_xml[0].attributes["WorkstationID"].value,
            "timestamp": timestamp}
        return message
    else:
        request_xml = comm_xml.getElementsByTagName("ServiceRequest")
        if len(request_xml) == 1:
            request = {
                "Class": "ServiceRequest",
                "ID": request_xml[0].attributes["RequestID"].value,
                "Type": request_xml[0].attributes["RequestType"].value,
                "WorkstationID": request_xml[0].attributes["WorkstationID"].value,
                "timestamp": timestamp}

            return request

    print("Unexpected log message format: " + comm_xml.toxml())
    return None


def parse_log(path):
    with io.open(path, "r", encoding="windows-1252") as f:
        lines = f.readlines()
    logs = []
    for line in lines:
        match = re.match(r"^([\d]{4}-[\d]{2}-[\d]{2} [\d]{2}:[\d]{2}:[\d]{2})\s+(\w+)\s+:\s+(.+): {{{$",
                         line)
        if match:
            log = {"timestamp": match.group(1),
                   "level": match.group(2),
                   "type": match.group(3),
                   "message": []}
        else:
            if line.startswith(r"}}}"):
                logs.append(log)
            elif len(line.strip()) > 0:
                log["message"].append(line.rstrip())
    return logs


def get_text(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc)


def create_comms_csv(target_file_path):
    with open(target_file_path, 'w', newline='') as csv_file:
        csv_writer = csv.writer(csv_file, delimiter=',',
                                quotechar='"', quoting=csv.QUOTE_ALL)

        csv_headers = ["Request Timestamp", "Class", "Type", "WorkstationID", "ID",
                       "Response Timestamp", "Result", "ErrorCode"]
        csv_writer.writerow(csv_headers)

        for comm in all_comms:
            request = comm["request"]
            request_columns = [request["timestamp"], request["Class"], request["Type"], request["WorkstationID"], request["ID"]]
            response_columns = ["", "", ""]
            if "response" in comm:
                response = comm["response"]
                response_columns = [response["timestamp"], response["Result"], response["ErrorCode"]]
            csv_columns = request_columns + response_columns
            csv_writer.writerow(csv_columns)


parser = ArgumentParser()
parser.add_argument("-d", "--dir", dest="directory",
                    help="Directory to scan for FDC log files.", metavar="DIR")

args = parser.parse_args()

for filename in os.listdir(args.directory):
    if filename.startswith("MessageTrace.log"):
        parse_comms_in_file(os.path.join(args.directory, filename))

create_comms_csv(os.path.join(args.directory, "comms.csv"))

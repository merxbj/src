import io, re, csv, os, json
from argparse import ArgumentParser

all_comms = []


def parse_comms_in_file(path):
    print("Processing " + path)

    logs = parse_log(path)
    file_comms, pos_number = parse_comms(logs)
    update_pos_number(file_comms, pos_number)

    all_comms.extend(file_comms)


def update_pos_number(file_comms, pos_number):
    if pos_number != 0:
        print("\tDetermined POS number from the contents of the log file:" + str(pos_number))
    else:
        print("\tUnable to determine POS Number from the contents of the log file!")
    for comm in file_comms:
        comm["pos"] = pos_number


def parse_comms(logs):
    pos_number = 0
    file_comms = []
    for log in logs:
        match = re.search(r"About to send request:", log["message"][0])
        if match:
            request, pos_number = parse_request(log, pos_number)
            file_comms.append({"request": request})
            continue

        response = None
        match = re.search(r"Received response:", log["message"][0])
        if match:
            response = parse_response(file_comms, log)
        else:
            match = re.search(r"Skipping response content parsing", log["message"][0])
            if match:
                response = parse_empty_response(file_comms, log)
        if response:
            assign_response_to_request(file_comms, response)
            continue

        match = re.search(r"Unable to refresh token", log["message"][0])
        if match:
            file_comms.append(build_failed_token_refresh(log, pos_number))
            continue

    return file_comms, pos_number


def assign_response_to_request(file_comms, response):
    matching_request_found = False
    for comm in file_comms[::-1]:
        if comm["request"]["thread"] == response["thread"]:
            comm["response"] = response
            matching_request_found = True
            break
    if not matching_request_found:
        print("Unexpected response found: " + str(response))


def parse_empty_response(file_comms, log):
    if len(log["message"]) == 1:
        return {
            "status": "OK EMPTY",
            "payload": "",
            "thread": log["thread"],
            "timestamp": log["timestamp"]}

    print("Unexpected log message format:") + log
    return None


def parse_response(file_comms, log):
    if len(log["message"]) > 2:
        status_match = re.search(r"< Status:(.+)$", log["message"][2])
        response = {
            "status": status_match.group(1),
            "payload": "",
            "thread": log["thread"],
            "timestamp": log["timestamp"]}
        if len(log["message"]) > 3:
            payload_match = re.search(r"<\s+(.+)$", log["message"][3])
            if payload_match:
                response["payload"] = payload_match.group(1)

        return response

    print("Unexpected log message format:") + log
    return None


def parse_request(log, pos_number):
    if len(log["message"]) > 1:
        http_request_match = re.search(r">\s+(.+)$", log["message"][1])
        url = http_request_match.group(1)
        endpoint = url.split('/')[-1]
        request = {
            "endpoint": endpoint,
            "payload": "",
            "thread": log["thread"],
            "timestamp": log["timestamp"]}
        if len(log["message"]) > 2:
            payload_match = re.search(r">\s+(.+)$", log["message"][2])
            if payload_match:
                request["payload"] = payload_match.group(1)

                # We still don't know the POS, let's try digging it out from this request
                if pos_number == 0:
                    payload = json.loads(request["payload"])
                    if "PointOfSaleData" in payload:
                        pos_number = payload["PointOfSaleData"]["PoSCashId"]

        # This will be either 0, if we haven't found the POS from payload yet
        # Or this is already some actual value if we found it previously
        # Or this is also some actual value that we might have just parsed out from this request
        request["pos"] = pos_number

        return request, pos_number

    print("Unexpected log message format:") + log
    return None, 0


def parse_log(path):
    with io.open(path, "r", encoding="windows-1252") as f:
        lines = f.readlines()
    logs = []
    for line in lines:
        match = re.match(r"^([\d]{4}-[\d]{2}-[\d]{2}T[\d]{2}:[\d]{2}:[\d]{2}\.[\d]{3})\s+(\w+)\s+(\d+)\s+(\w+)\s+(.+)$",
                         line)
        if match:
            log = {"timestamp": match.group(1),
                   "level": match.group(2),
                   "thread": match.group(3),
                   "module": match.group(4),
                   "message": [match.group(5)]}
            logs.append(log)
        else:
            logs[-1]["message"].append(line.rstrip())
    return logs


def build_failed_token_refresh(log, pos_number):
    request = {
        "endpoint": "RefreshToken",
        "payload": "",
        "thread": log["thread"],
        "timestamp": log["timestamp"],
        "pos": pos_number}

    response = {
        "status": "FAILURE",
        "payload": log["message"][0],
        "thread": log["thread"],
        "timestamp": log["timestamp"]}

    return {"request": request, "response": response}


def create_comms_csv(target_file_path):
    with open(target_file_path, 'w', newline='') as csv_file:
        csv_writer = csv.writer(csv_file, delimiter=',',
                                quotechar='"', quoting=csv.QUOTE_ALL)

        csv_headers = ["POS", "Request Timestamp", "Endpoint", "Payload",
                       "Status", "Response Timestamp", "Response Payload"]
        csv_writer.writerow(csv_headers)

        for comm in all_comms:
            request = comm["request"]
            request_columns = [request["timestamp"], request["endpoint"], request["payload"]]
            response_columns = ["", "", ""]
            if "response" in comm:
                response = comm["response"]
                response_columns = [response["status"], response["timestamp"], response["payload"]]
            csv_columns = [comm["pos"]] + request_columns + response_columns
            csv_writer.writerow(csv_columns)


parser = ArgumentParser()
parser.add_argument("-d", "--dir", dest="directory",
                    help="Directory to scan for ECP log files.", metavar="DIR")

args = parser.parse_args()

for filename in os.listdir(args.directory):
    if filename.startswith("PosCacheESIECPAdapter.log") and filename.endswith((".bak", ".log")):
        parse_comms_in_file(os.path.join(args.directory, filename))

create_comms_csv(os.path.join(args.directory, "comms.csv"))

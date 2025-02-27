import argparse
import logging
import os
import sys
import traceback
from pathlib import Path
from logging.handlers import RotatingFileHandler

import json

import requests
from requests.auth import HTTPBasicAuth

import random

import time


def get_workdir_path():
    return os.path.join(str(Path.home()), "jira")


def urljoin(*args):
    """
    Joins given arguments into an url. Trailing but not leading slashes are
    stripped for each argument.
    """

    return "/".join(map(lambda x: str(x).rstrip('/'), args))


# Returns a dictionary where:
#  key = Jira Field Id of the NORMAL field
#  value = Jira Field Id of the (MIGRATED) field
def build_allowed_values_map(allowed_values, migrated_allowed_values):
    allowed_values_map = {}
    for allowed_value in allowed_values:
        if "value" in allowed_value:
            allowed_values_map[allowed_value["value"]] = {"normal": allowed_value, "migrated": None}
        elif "name" in allowed_value:
            allowed_values_map[allowed_value["name"]] = {"normal": allowed_value, "migrated": None}
        else:
            logging.warning("Unexpected format of an allowed value! {}".format(allowed_value))

    for migrated_allowed_value in migrated_allowed_values:
        if "value" in migrated_allowed_value:
            key = migrated_allowed_value["value"]
        elif "name" in migrated_allowed_value:
            key = migrated_allowed_value["name"]
        else:
            key = None
            logging.warning("Unexpected format of an migrated allowed value! {}".format(migrated_allowed_value))

        if key in allowed_values_map:
            allowed_values_map[key]["migrated"] = migrated_allowed_value
        else:
            logging.warning("Could map migrated allowed value {} to a normal allowed value! Normal allowed value missing!")

    return allowed_values_map


def update_field_map(issue_meta, field_map, issue_type):

    migrated_fields = {}
    for key, value in issue_meta["fields"].items():
        field_name = value["name"]
        if field_name.endswith(" (migrated)"):
            normalized_field_name = str.removesuffix(field_name, " (migrated)")
            if "allowedValues" in value:
                migrated_fields[normalized_field_name] = (key, value["allowedValues"])
            else:
                migrated_fields[normalized_field_name] = (key, None)



    field_map[issue_type] = {}
    for key, value in issue_meta["fields"].items():
        field_name = value["name"]
        if field_name in migrated_fields:
            (migrated_field_id, migrated_allowed_values) = migrated_fields[field_name]
            field_map[issue_type][key] = {"id": migrated_field_id}
            logging.debug("Mapping field for {}: '{}' ('{}') to '{}' ('{}')".format(issue_type, key, field_name, migrated_field_id, field_name + " (migrated)"))

            if "allowedValues" in value and len(value["allowedValues"]) > 0:
                if migrated_allowed_values is not None and len(migrated_allowed_values) > 0:
                    allowed_values_map = build_allowed_values_map(value["allowedValues"], migrated_allowed_values)
                    field_map[issue_type][key]["allowedValuesMap"] = allowed_values_map
                    for key, value in allowed_values_map.items():
                        logging.debug("\tMapping allowed value of {}: '{}' to '{}'".format(key, value["normal"]["id"], value["migrated"]["id"]))


def map_field_value(migrated_value, allowed_values_map):
    if isinstance(migrated_value, list):
        mapped_list = []
        for item in migrated_value:
            mapped_list.append(map_field_value(item, allowed_values_map))
        return mapped_list

    if "value" in migrated_value:
        key = migrated_value["value"]
    elif "name" in migrated_value:
        key = migrated_value["name"]
    else:
        key = None
        logging.warning("Unexpected format of a migrated value! {}".format(migrated_value))

    if key in allowed_values_map:
        mapping = allowed_values_map[key]
        return mapping["normal"]

def build_update_request(issue, field_map):
    request = { "fields": {} }

    issue_type = issue["fields"]["issuetype"]["name"]

    for normal, migrated_info in field_map[issue_type].items():
        migrated = migrated_info["id"]
        if normal in issue["fields"] and migrated in issue["fields"]:
            if issue["fields"][migrated] is not None:

                mapped_value = issue["fields"][migrated]
                if "allowedValuesMap" in migrated_info:
                    mapped_value = map_field_value(issue["fields"][migrated], migrated_info["allowedValuesMap"])

                update_field = mapped_value

                request["fields"][normal] = update_field

    logging.info("Mapping issue {} values as follows:\n{}".format(issue["key"], json.dumps(request, indent=4)))

    return request


def rate_limited_get(*args, **kwargs):
    # Defaults may vary based on the app use case and APIs being called.
    max_retries = 10  # Should be 0 to disable (e.g. API is not idempotent)
    retry_count = 0
    last_retry_delay_millis = 5000
    max_retry_delay_millis = 30000
    jitter_multiplier_range = [0.7, 1.3]

    # Re-entrant logic to send a request and process the response...
    response =  requests.get(*args, **kwargs)
    if response.ok:
        return response
    else:
        retry_delay_millis = -1
        if "Retry-After" in response.headers:
            retry_delay_millis = 1000 * response.headers["Retry-After"]
        elif response.status_code == 429:
            retry_delay_millis = min(2 * last_retry_delay_millis, max_retry_delay_millis)

        if retry_delay_millis > 0 and retry_count < max_retries:
            retry_delay_millis += retry_delay_millis * random.uniform(*jitter_multiplier_range)
            time.sleep(retry_delay_millis)
            retry_count += 1
            return rate_limited_get(*args, **kwargs)
        else:
            return response


def rate_limited_put(*args, **kwargs):
    # Defaults may vary based on the app use case and APIs being called.
    max_retries = 10  # Should be 0 to disable (e.g. API is not idempotent)
    retry_count = 0
    last_retry_delay_millis = 5000
    max_retry_delay_millis = 30000
    jitter_multiplier_range = [0.7, 1.3]

    # Re-entrant logic to send a request and process the response...
    response = requests.put(*args, **kwargs)
    if response.ok:
        return response
    else:
        retry_delay_millis = -1
        if "Retry-After" in response.headers:
            retry_delay_millis = 1000 * response.headers["Retry-After"]
        elif response.status_code == 429:
            retry_delay_millis = min(2 * last_retry_delay_millis, max_retry_delay_millis)

        if retry_delay_millis > 0 and retry_count < max_retries:
            retry_delay_millis += retry_delay_millis * random.uniform(*jitter_multiplier_range)
            time.sleep(retry_delay_millis)
            retry_count += 1
            return rate_limited_get(*args, **kwargs)
        else:
            return response


def perform_consolidation():
    basic_auth = HTTPBasicAuth(args.jira_user, args.jira_password)
    put_headers = {"Content-Type": "application/json"}

    field_map = {}

    for issue_number in range(args.jira_issue_start, args.jira_issue_end + 1):
        issue_key = "{}-{}".format(args.jira_project_key, issue_number)
        try:

            full_url = urljoin(args.jira_url, "rest/api/3/issue/", issue_key)
            get_response = rate_limited_get(full_url, auth=basic_auth)

            if get_response.ok:
                issue = get_response.json()
                issue_type = issue["fields"]["issuetype"]["name"]
                if issue_type not in field_map:
                    meta_url = urljoin(full_url, "editmeta")
                    get_meta_response = rate_limited_get(meta_url + "?expand=names", auth=basic_auth)
                    if get_meta_response.ok:
                        issue_meta = get_meta_response.json()
                        update_field_map(issue_meta, field_map, issue_type)
                    else:
                        logging.error(
                            "Failed to retrieve issue fields metadata {}! Status={}, Response={}".format(issue_key,
                                                                                       get_meta_response.status_code,
                                                                                       get_meta_response.text))
                        continue

                put_request_data = build_update_request(issue, field_map)

                if not args.dry:
                    put_response = rate_limited_put(full_url, data=json.dumps(put_request_data), auth=basic_auth,
                                                headers=put_headers)
                    if put_response.ok:
                        logging.info("Successfully updated issue {}!".format(issue_key))
                    else:
                        logging.error(
                            "Failed to update issue {}! Status={}, Response={}".format(issue_key, put_response.status_code,
                                                                                       put_response.text))

        except:
            logging.error("Failed to process issue {}: {}".format(issue_key, traceback.format_exc()))


if __name__ == '__main__':

    if not os.path.exists(get_workdir_path()):
        os.makedirs(get_workdir_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_workdir_path(), "field_migration.log"), encoding="utf-8",
                            maxBytes=10485760, backupCount=20),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.DEBUG,
        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")

    parser = argparse.ArgumentParser(description='Controls the pool filtration pump.')
    parser.add_argument("--jira_url", dest="jira_url", type=str,
                        help="Base URL for the Jira Cloud.")
    parser.add_argument("--jira_user", dest="jira_user", type=str,
                        help="User name to Basic Auth with the Jira Cloud API")
    parser.add_argument("--jira_password", dest="jira_password", type=str,
                        help="Password for the Basic Auth with the Jira Cloud API (API token)")

    parser.add_argument("--jira_project_key", dest="jira_project_key", type=str,
                        help="Key of the project to consolidate fields in.")
    parser.add_argument("--jira_issue_start", dest="jira_issue_start", type=int,
                        help="First issue number for batch processing.")
    parser.add_argument("--jira_issue_end", dest="jira_issue_end", type=int,
                        help="Last issue number for batch processing.")

    parser.add_argument("--save_file_name", dest="save_file_name", type=str,
                        help="File name of the progress save file.")

    parser.add_argument('--force_restart', default=False, dest="force_restart", action=argparse.BooleanOptionalAction)
    parser.add_argument('--dry', dest="dry", default=False, action=argparse.BooleanOptionalAction)

    args = parser.parse_args()

    perform_consolidation()

    # time.sleep(1)

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


customers_map = {
    "Amic": [
        {
            "self": "https://ncrvoyix-saas.atlassian.net/rest/api/3/customFieldOption/45975",
            "value": "AMIC POLSKA SP. Z O.O.",
            "id": "45975"
        },
    ],
    "DanskFuels": [
        {
            "self": "https://ncrvoyix-saas.atlassian.net/rest/api/3/customFieldOption/46952",
            "value": "DanskFuels",
            "id": "46952"
        }
    ],
    "ENOC": [
        {
            "self": "https://ncrvoyix-saas.atlassian.net/rest/api/3/customFieldOption/47205",
            "value": "ENOC",
            "id": "47205"
        }
    ],
    "OKQ8": [
        {
            "self": "https://ncrvoyix-saas.atlassian.net/rest/api/3/customFieldOption/48502",
            "value": "OKQ8",
            "id": "48502"
        }
    ],
    "Repsol": [
        {
            "self": "https://ncrvoyix-saas.atlassian.net/rest/api/3/customFieldOption/48778",
            "value": "Repsol YPF",
            "id": "48778"
        }
    ],
    "Shell": [
        {
            "self": "https://ncrvoyix-saas.atlassian.net/rest/api/3/customFieldOption/49000",
            "value": "Shell Czech Republic a.s.",
            "id": "49000"
        },
        {
            "self": "https://ncrvoyix-saas.atlassian.net/rest/api/3/customFieldOption/49004",
            "value": "SHELL Slovakia s.r.o.",
            "id": "49004"
        },
    ]
}


def get_workdir_path():
    return os.path.join(str(Path.home()), "jira")


def urljoin(*args):
    """
    Joins given arguments into an url. Trailing but not leading slashes are
    stripped for each argument.
    """

    return "/".join(map(lambda x: str(x).rstrip('/'), args))


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


def build_update_request(issue, customers_to_assign):
    request = { "fields": {} }

    request["fields"]["customfield_10248"] = customers_to_assign

    return request


def fetch_issues_without_customer(auth):

    next_page_token = None
    issues = []

    while True:
        params = {
            "jql": r'project = "CFR RPOS EMEA" AND "Originating Customer" IS EMPTY',
            "fields": "key, issuetype, assignee, customfield_10248, summary, fixVersions, priority, status, customfield_10527, customfield_10517",
            "maxResults": 500,
            "nextPageToken": next_page_token
        }

        full_url = urljoin(args.jira_url, "rest/api/3/search/jql".format(args.jira_project_key))
        get_response = rate_limited_get(full_url, auth=auth, params=params)
        if not get_response.ok:
            logging.error(
                "Failed to get list of all issues without Originating Customer for {}! Status: {}, Error(s): {}".format(args.jira_project_key, get_response.status_code, get_response.text))
            return []

        data = get_response.json()
        if "issues" not in data:
            logging.error(
                "Invalid/Unexpected response while getting list of all issues without Originating Customer for {}! Status: {}, Error(s): {}".format(
                    args.jira_project_key),
                get_response.status_code, get_response.text)
            return []

        issues.extend(data["issues"])

        if "nextPageToken" in data and data["nextPageToken"] is not None:
            next_page_token = data["nextPageToken"]
        else:
            break

    logging.debug("Found {} issues without Originating Customer for {}!".format(len(issues), args.jira_project_key))
    return issues


def auto_detect_customer(issue_data):
    summary = issue_data["summary"].lower()
    for customer, value in customers_map.items():
        if customer.lower() in summary:
            return value

    return []


def auto_assign_originating_customer():
    basic_auth = HTTPBasicAuth(args.jira_user, args.jira_password)
    put_headers = {"Content-Type": "application/json"}

    issues_to_auto_assign = fetch_issues_without_customer(basic_auth)

    for issue in issues_to_auto_assign:
        fields = issue["fields"]
        issue_data = {
            "key": issue["key"],
            "type": fields["issuetype"]["name"],
            "summary": fields["summary"],
            "releases": "\n".join([fix_version["name"] for fix_version in fields["fixVersions"]]),
            "priority": fields["priority"]["name"],
            "status": fields["status"]["name"],
            "issue_link": urljoin(args.jira_url, "browse/{}".format(issue["key"]))
        }

        customers = auto_detect_customer(issue_data)
        customers_str = ",".join([customer["value"] for customer in customers])

        if len(customers) >0:
            logging.debug("Auto-detected customer(s): {} for {} - {}".format(customers_str, issue_data["key"],
                                                                             issue_data["summary"]))
        else:
            continue

        try:

            full_url = urljoin(args.jira_url, "rest/api/3/issue/", issue_data["key"])
            put_request_data = build_update_request(issue, customers)

            if not args.dry:
                put_response = rate_limited_put(full_url, data=json.dumps(put_request_data), auth=basic_auth,
                                            headers=put_headers)
                if put_response.ok:
                    logging.info("Successfully updated issue {}!".format(issue_data["key"]))
                else:
                    logging.error(
                        "Failed to update issue {}! Status={}, Response={}".format(issue_data["key"], put_response.status_code,
                                                                                   put_response.text))

        except:
            logging.error("Failed to process issue {}: {}".format(issue_data["key"], traceback.format_exc()))


if __name__ == '__main__':

    if not os.path.exists(get_workdir_path()):
        os.makedirs(get_workdir_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_workdir_path(), "originating_customer_fix.log"), encoding="utf-8",
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

    parser.add_argument('--dry', dest="dry", default=False, action=argparse.BooleanOptionalAction)

    args = parser.parse_args()

    auto_assign_originating_customer()

    # time.sleep(1)

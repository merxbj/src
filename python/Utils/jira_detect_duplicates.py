import argparse
import json
import logging
import os
import os.path
import sys
from pathlib import Path
from logging.handlers import RotatingFileHandler

import requests
from requests.auth import HTTPBasicAuth

import random
from difflib import SequenceMatcher

import time


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


def fetch_external_issues(auth):

    next_page_token = None
    issues = []

    while True:
        params = {
            "jql": r'project = "CFR RPOS EMEA" AND issuetype in (Bug, Incident) AND "Actual Discovery Stage" in ("SE11 PS Solution Development and Testing", "SE12 Customer Lab", "SE13 Customer Controlled Deployment", "SE14 Customer Production") AND (issuetype = Incident OR "Merge Issue?" in (No, EMPTY))',
            "fields": "key, issuetype, assignee, customfield_10248, summary, fixVersions, priority, status, customfield_10527, customfield_10517, customfield_10260",
            "maxResults": 500,
            "nextPageToken": next_page_token
        }

        full_url = urljoin(args.jira_url, "rest/api/3/search/jql".format(args.jira_project_key))
        get_response = rate_limited_get(full_url, auth=auth, params=params)
        if not get_response.ok:
            logging.error(
                "Failed to get list of all external issues for {}! Status: {}, Error(s): {}".format(args.jira_project_key, get_response.status_code, get_response.text))
            return []

        data = get_response.json()
        if "issues" not in data:
            logging.error(
                "Invalid/Unexpected response while getting list of all external issuesfor {}! Status: {}, Error(s): {}".format(
                    args.jira_project_key),
                get_response.status_code, get_response.text)
            return []

        issues.extend(data["issues"])

        if "nextPageToken" in data and data["nextPageToken"] is not None:
            next_page_token = data["nextPageToken"]
        else:
            break

    logging.debug("Found {} external issues for {}!".format(len(issues), args.jira_project_key))
    return issues


def similar(a, b):
    return SequenceMatcher(None, a, b).ratio()


def detect_improperly_cloned_external_issues():
    basic_auth = HTTPBasicAuth(args.jira_user, args.jira_password)

    if len(args.cache_file) and os.path.isfile(args.cache_file) == 0:
        issues_to_scan = fetch_external_issues(basic_auth)
        with open(args.cache_file, "w") as cache_file:
            json.dump(issues_to_scan, cache_file, indent=4, sort_keys=True)
    else:
        with open(args.cache_file, "r") as cache_file:
            issues_to_scan = json.load(cache_file)

    for i in range(0, len(issues_to_scan)):
        issue_one = issues_to_scan[i]
        for j in range(i+1, len(issues_to_scan)):
            issue_two = issues_to_scan[j]
            ratio = similar(issue_one["fields"]["summary"], issue_two["fields"]["summary"])
            if ratio > 0.9:
                logging.debug("Similar issues ({}):\n\t{} - {}\n\t{} - {}".format(ratio, issue_one["key"], issue_one["fields"]["summary"], issue_two["key"], issue_two["fields"]["summary"]))


if __name__ == '__main__':

    if not os.path.exists(get_workdir_path()):
        os.makedirs(get_workdir_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_workdir_path(), "duplicate_detection.log"), encoding="utf-8",
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

    parser.add_argument("--cache_file", dest="cache_file", type=str,
                        help="Path to the file where the data cached from JC can be loaded from.")

    args = parser.parse_args()

    detect_improperly_cloned_external_issues()

    # time.sleep(1)

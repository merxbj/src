import argparse
import datetime
import logging
import os
import sys
from pathlib import Path
from logging.handlers import RotatingFileHandler

# web server
from flask import Flask, render_template, g

import requests
from requests.auth import HTTPBasicAuth

import random

import time

app = Flask(__name__)


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


def fetch_old_open_releases():

    old_releases = []

    full_url = urljoin(args.jira_url, "rest/api/3/project/{}/versions/".format(args.jira_project_key))
    get_response = rate_limited_get(full_url, auth=jira_basic_auth)
    if not get_response.ok:
        logging.error(
            "Failed to get list of all releases for {}! Status: {}, Error(s): {}".format(args.jira_project_key),
            get_response.status_code, get_response.text)

    releases = get_response.json()
    for release in releases:
        if release["released"] or release["archived"]:
            continue

        if release["name"].lower().endswith(".next"):
            logging.debug("Skipping .NEXT release: {}".format(release["name"], str(release_age)))
            continue

        if "releaseDate" not in release or len(release["releaseDate"]) == 0:
            logging.warning("Skipping release without release date: {}".format(release["name"]))
            continue

        try:
            release_date = datetime.datetime.strptime(release["releaseDate"], "%Y-%m-%d")
        except:
            logging.error(
                "Failed to parse releaseDate field ({}) of release {}".format(release["releaseDate"], release["name"]))
            continue

        release_age = datetime.datetime.now() - release_date
        if release_age <= datetime.timedelta(days=60):
            logging.debug(
                "Skipping release {} - it's still too young ({}).".format(release["name"], str(release_age)))
            continue

        logging.info("Old Release: {} - {}".format(release["name"], release_date))
        old_releases.append(release)

    return old_releases


def fetch_old_issues(old_releases_ids):

    if len(old_releases_ids) == 0:
        return[]

    params = {
        "jql": r"project = {} AND fixVersion in ({}) AND Status not in (Done, Withdrawn)".format(args.jira_project_key, ",".join(map(str, old_releases_ids))),
        "fields": "key, issuetype, assignee, customfield_10248, summary, fixVersions, priority, status, customfield_10232"
    }

    full_url = urljoin(args.jira_url, "rest/api/2/search")
    get_response = rate_limited_get(full_url, auth=jira_basic_auth, params=params)
    if not get_response.ok:
        logging.error(
            "Failed to get list of all old release open issues for {}! Status: {}, Error(s): {}".format(args.jira_project_key, get_response.status_code, get_response.text))
        return []

    data = get_response.json()
    if "issues" not in data:
        logging.error(
            "Invalid/Unexpected response while getting list of all old release open issues for {}! Status: {}, Error(s): {}".format(
                args.jira_project_key),
            get_response.status_code, get_response.text)
        return []

    return data["issues"]


def summarize_old_issues(issues):
    old_issue_summary = {
        "recipients": set(),
        "customer_issues": {},
        "issue_count": 0
    }

    for issue in issues:
        fields = issue["fields"]
        issue_data = {
            "key": issue["key"],
            "type": fields["issuetype"]["name"],
            "assignee": fields["assignee"]["displayName"],
            "summary": fields["summary"],
            "releases": "\n".join([fix_version["name"] for fix_version in fields["fixVersions"]]),
            "release_dates": "\n".join([fix_version["releaseDate"] for fix_version in fields["fixVersions"]]),
            "priority": fields["priority"]["name"],
            "status": fields["status"]["name"],
            "issue_link": urljoin(args.jira_url, "browse/{}".format(issue["key"]))
        }

        if "customfield_10232" not in fields or fields["customfield_10232"] is None:
            issue_data["severity"] = "N/A"
        else:
            issue_data["severity"] = fields["customfield_10232"]["value"]

        old_issue_summary["recipients"].add(fields["assignee"]["emailAddress"].replace("@ncr.com", "@ncrvoyix.com"))

        if "customfield_10248" not in fields or fields["customfield_10248"] is None:
            customers = "N/A"
        else:
            customers = "\n".join([originatingCustomer["value"] for originatingCustomer in fields["customfield_10248"]])

        if customers not in old_issue_summary["customer_issues"]:
            old_issue_summary["customer_issues"][customers] = {}
            old_issue_summary["customer_issues"][customers]["issues"] = []

        old_issue_summary["customer_issues"][customers]["issues"].append(issue_data)

    for customers_name, customer_issues in old_issue_summary["customer_issues"].items():
        customer_issues["issue_count"] = len(customer_issues["issues"])
        customer_issues["issues"] = sorted(customer_issues["issues"], key=lambda issue: issue["release_dates"])
        old_issue_summary["issue_count"] = old_issue_summary["issue_count"] + customer_issues["issue_count"]

    return old_issue_summary


def fetch_open_issues_in_old_releases_summary():

    # Get list of all releases (Versions)
    old_releases = fetch_old_open_releases()
    old_releases_ids = [int(old_release["id"]) for old_release in old_releases]

    # Get list of all open issues in the old releases
    old_issues = fetch_old_issues(old_releases_ids)

    logging.debug("List of all old issues: {}".format([old_issue["key"] for old_issue in old_issues]))

    return summarize_old_issues(old_issues)


def fetch_estimated_epics():

    params = {
        "jql": r"project = {} AND issueType IN (Epic) AND originalEstimate > 0".format(args.jira_project_key),
        "fields": "key, summary, status, timetracking"
    }

    full_url = urljoin(args.jira_url, "rest/api/2/search")
    get_response = rate_limited_get(full_url, auth=jira_basic_auth, params=params)
    if not get_response.ok:
        logging.error(
            "Failed to get list of all estimated epics for {}! Status: {}, Error(s): {}".format(args.jira_project_key, get_response.status_code, get_response.text))
        return []

    data = get_response.json()
    if "issues" not in data:
        logging.error(
            "Invalid/Unexpected response while getting list of all estimated epics for {}! Status: {}, Error(s): {}".format(
                args.jira_project_key),
            get_response.status_code, get_response.text)
        return []

    return data["issues"]


def prepare_e2a_for_epic(epic):
    fields = epic["fields"]
    estimate = fields["timetracking"]["originalEstimateSeconds"] / 60 / 60
    actual = fields["timetracking"]["timeSpentSeconds"] / 60 / 60 if "timeSpentSeconds" in fields["timetracking"] else 0.0
    epic_e2a_data = {
        "issue_key": epic["key"],
        "issue_id": epic["id"],
        "issue_link": urljoin(args.jira_url, "browse/{}".format(epic["key"])),
        "issue_summary": fields["summary"],
        "issue_status": fields["status"]["name"],
        "account_status": "N/A",
        "estimate": estimate,
        "actual": actual,
        "over_under": estimate - actual,
        "over_under_perc": (1.0 - (actual / estimate)) * 100.0
    }

    return epic_e2a_data

def prepare_e2a():
    e2a_data = {
        "active_features": {
            "epics": [],
            "total": {
                "estimate": 0.0, "actual": 0.0, "over_under": 0.0, "over_under_perc": 0.0
            }
        },
        "completed_features": {
            "epics": [],
            "total": {
                "estimate": 0.0, "actual": 0.0, "over_under": 0.0, "over_under_perc": 0.0
            }
        },
        "total": {
            "estimate": 0.0, "actual": 0.0, "over_under": 0.0, "over_under_perc": 0.0
        },
        "unapproved_features": {
            "epics": [],
            "total": {
                "estimate": 0.0, "actual": 0.0, "over_under": 0.0, "over_under_perc": 0.0
            }
        }
    }
    estimated_epics = fetch_estimated_epics()

    if len(estimated_epics) == 0:
        return e2a_data

    logging.debug(estimated_epics)

    for epic in estimated_epics:
        epic_e2a_data = prepare_e2a_for_epic(epic)
        if epic_e2a_data["issue_status"] == "On Hold":
            e2a_data["unapproved_features"]["epics"].append(epic_e2a_data)

            e2a_data["unapproved_features"]["total"]["estimate"] += epic_e2a_data["estimate"]
            e2a_data["unapproved_features"]["total"]["actual"] += epic_e2a_data["actual"]
            e2a_data["unapproved_features"]["total"]["over_under"] += epic_e2a_data["estimate"] - epic_e2a_data["actual"]
        elif epic_e2a_data["issue_status"] == "Done":
            e2a_data["completed_features"]["epics"].append(epic_e2a_data)

            e2a_data["completed_features"]["total"]["estimate"] += epic_e2a_data["estimate"]
            e2a_data["completed_features"]["total"]["actual"] += epic_e2a_data["actual"]
            e2a_data["completed_features"]["total"]["over_under"] += epic_e2a_data["estimate"] - epic_e2a_data["actual"]
        else:
            e2a_data["active_features"]["epics"].append(epic_e2a_data)

            e2a_data["active_features"]["total"]["estimate"] += epic_e2a_data["estimate"]
            e2a_data["active_features"]["total"]["actual"] += epic_e2a_data["actual"]
            e2a_data["active_features"]["total"]["over_under"] += epic_e2a_data["estimate"] - epic_e2a_data["actual"]

        if epic_e2a_data["issue_status"] != "On Hold":
            e2a_data["total"]["estimate"] += epic_e2a_data["estimate"]
            e2a_data["total"]["actual"] += epic_e2a_data["actual"]
            e2a_data["total"]["over_under"] += epic_e2a_data["estimate"] - epic_e2a_data["actual"]

    if len(e2a_data["active_features"]["epics"]) > 0:
        e2a_data["active_features"]["total"]["over_under_perc"] = (1.0 - (e2a_data["active_features"]["total"]["actual"] / e2a_data["active_features"]["total"]["estimate"])) * 100.0
        e2a_data["active_features"]["epics"] = sorted(e2a_data["active_features"]["epics"],
                                                      key=lambda epic: epic["over_under_perc"])

    if len(e2a_data["completed_features"]["epics"]) > 0:
        e2a_data["completed_features"]["total"]["over_under_perc"] = (1.0 - (e2a_data["completed_features"]["total"]["actual"] / e2a_data["completed_features"]["total"]["estimate"])) * 100.0
        e2a_data["completed_features"]["epics"] = sorted(e2a_data["completed_features"]["epics"], key=lambda epic: epic["over_under_perc"])

    if len(e2a_data["unapproved_features"]["epics"]) > 0:
        e2a_data["unapproved_features"]["total"]["over_under_perc"] = (1.0 - (e2a_data["unapproved_features"]["total"]["actual"] / e2a_data["unapproved_features"]["total"]["estimate"])) * 100.0
        e2a_data["unapproved_features"]["epics"] = sorted(e2a_data["unapproved_features"]["epics"], key=lambda epic: epic["over_under_perc"])

    e2a_data["total"]["over_under_perc"] = (1.0 - (e2a_data["total"]["actual"] / e2a_data["total"]["estimate"])) * 100.0

    return e2a_data


def fetch_epic_data(epic_id):

    full_url = urljoin(args.jira_url, "rest/api/3/issue/", epic_id)
    get_response = rate_limited_get(full_url, auth=jira_basic_auth)
    if not get_response.ok:
        logging.error(
            "Failed to get epic data for {}! Status: {}, Error(s): {}".format(epic_id, get_response.status_code, get_response.text))
        return []

    return get_response.json()


def prepare_e2a_for_epic_id(epic_id):
    epic_data = fetch_epic_data(epic_id)
    return prepare_e2a_for_epic(epic_data)


def fetch_epic_worklog(epic_id):

    headers = { "Authorization": "Bearer {}".format(args.tempo_token) }

    params = {"limit": 500}
    full_url = urljoin(args.tempo_url, "4/worklogs/issue/", epic_id)

    get_response = rate_limited_get(full_url, headers=headers, params=params, verify = False)
    if not get_response.ok:
        logging.error(
            "Failed to get worklog data for {}! Status: {}, Error(s): {}".format(epic_id, get_response.status_code,
                                                                              get_response.text))
        return []

    worklog_data = get_response.json()

    return worklog_data["results"]


def fetch_jira_user(account_id):

    params = { "accountId": account_id }

    full_url = urljoin(args.jira_url, "rest/api/2/user")
    get_response = rate_limited_get(full_url, auth=jira_basic_auth, params=params)
    if not get_response.ok:
        logging.error(
            "Failed to get jira user data for {}! Status: {}, Error(s): {}".format(account_id, get_response.status_code, get_response.text))
        return None

    return get_response.json()


def resolve_jira_user(account_id):
    if account_id not in jira_user_cache:
        user_data = fetch_jira_user(account_id)
        display_name = account_id
        if user_data is not None and "displayName" in user_data:
            display_name = user_data["displayName"]
            logging.debug("Successfully identified Jira user {} as {}".format(account_id, display_name))
        jira_user_cache[account_id] = display_name

    return jira_user_cache[account_id]


def prepare_worklog_summary(epic_id):

    worklog_summary = {}

    worklog_data = fetch_epic_worklog(epic_id)
    for worklog in worklog_data:
        author = resolve_jira_user(worklog["author"]["accountId"])
        time = worklog["timeSpentSeconds"]
        if author not in worklog_summary:
            worklog_summary[author] = time / 60 / 60
        else:
            worklog_summary[author] += time / 60 / 60

    return worklog_summary


@app.route('/e2a/<epic_id>')
def e2a_summary(epic_id):
    return render_template("worklog_summary.html", e2a=prepare_e2a_for_epic_id(epic_id), ws=prepare_worklog_summary(epic_id))


@app.route('/e2a')
def e2a():
    return render_template("estimates-2-actuals.html", e2a=prepare_e2a())


@app.route('/old-releases')
def old_releases():
    return render_template("old-releases.html", issue_summary=fetch_open_issues_in_old_releases_summary())


if __name__ == '__main__':

    if not os.path.exists(get_workdir_path()):
        os.makedirs(get_workdir_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_workdir_path(), "release_management.log"), encoding="utf-8",
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
    parser.add_argument("--tempo_url", dest="tempo_url", type=str,
                        help="Base URL for the Tempo.")
    parser.add_argument("--tempo_token", dest="tempo_token", type=str,
                        help="Token for the Tempo API")

    args = parser.parse_args()

    jira_basic_auth = HTTPBasicAuth(args.jira_user, args.jira_password)

    jira_user_cache = {}

    app.run(debug=True, host="0.0.0.0", port=8081)

import argparse
import datetime
import logging
import os
import re
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


def fetch_open_main_releases():

    open_releases = []
    main_next_release = None

    full_url = urljoin(args.jira_url, "rest/api/3/project/{}/versions/".format(args.jira_project_key))
    get_response = rate_limited_get(full_url, auth=jira_basic_auth)
    if not get_response.ok:
        logging.error(
            "Failed to get list of all releases for {}! Status: {}, Error(s): {}".format(args.jira_project_key,
            get_response.status_code, get_response.text))

    releases = get_response.json()
    for release in releases:
        if release["released"] or release["archived"]:
            continue

        if release["name"].lower() == "rpos_6.10.c3.next":
            logging.debug("Found MAIN .NEXT release: {}".format(release["name"]))
            main_next_release = release
            continue

        if release["name"].lower().endswith(".next"):
            logging.debug("Skipping .NEXT release: {}".format(release["name"]))
            continue

        if not re.match(r".*6\.10\.c3\.(\d+)$", release["name"].lower()):
            logging.debug("Skipping non-main release: {}".format(release["name"]))
            continue

        logging.info("Open Main Release: {}".format(release["name"]))
        open_releases.append(release)

    return open_releases, main_next_release


def fetch_unfinished_releases(days_old):

    open_releases = []

    full_url = urljoin(args.jira_url, "rest/api/3/project/{}/versions/".format(args.jira_project_key))
    get_response = rate_limited_get(full_url, auth=jira_basic_auth)
    if not get_response.ok:
        logging.error(
            "Failed to get list of all releases for {}! Status: {}, Error(s): {}".format(args.jira_project_key,
            get_response.status_code, get_response.text))

    releases = get_response.json()
    for release in releases:
        if release["released"] or release["archived"]:
            continue

        if release["name"].lower().endswith(".next"):
            logging.debug("Skipping .NEXT release: {}".format(release["name"]))
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
        if release_age <= datetime.timedelta(days=days_old):
            logging.debug(
                "Skipping release {} - it's still too young ({}).".format(release["name"], str(release_age)))
            continue

        logging.info("Open Release: {}".format(release["name"]))
        open_releases.append(release)

    return open_releases


def fetch_open_issues(old_releases_ids, main_next_release_id):

    if len(old_releases_ids) == 0:
        return[]

    issues = []
    next_page_token = None

    full_url = urljoin(args.jira_url, "rest/api/3/search/jql")
    while True:

        if main_next_release_id is None:
            jql = r"project = {} AND fixVersion in ({}) AND Status not in (Done, Withdrawn)".format(
                args.jira_project_key, ",".join(map(str, old_releases_ids)))
        else:
            jql = (r'project = {} AND ((fixVersion in ({}) AND Status not in (Done, Withdrawn)) OR '
                   r'(fixVersion in ({}) AND Status NOT IN ('
                   r'"Done", "Withdrawn", "In Dev", "Not Started", "Ready For Dev", "In Triage", "On Hold", "In Analysis", "Ready for Build")))').format(
                args.jira_project_key, ",".join(map(str, old_releases_ids)), main_next_release_id)

        params = {
            "jql": jql,
            "fields": "key, issuetype, assignee, customfield_10248, summary, fixVersions, priority, status, customfield_10232",
            "nextPageToken": next_page_token
        }

        get_response = rate_limited_get(full_url, auth=jira_basic_auth, params=params)
        if not get_response.ok:
            logging.error(
                "Failed to get list of all old release open issues for {}! Status: {}, Error(s): {}".format(args.jira_project_key, get_response.status_code, get_response.text))
            return []

        data = get_response.json()
        if "issues" not in data:
            logging.error(
                "Invalid/Unexpected response while getting list of all old release open issues for {}! Status: {}, Error(s): {}".format(
                    args.jira_project_key,
                get_response.status_code, get_response.text))
            return []

        issues.extend(data["issues"])

        if "nextPageToken" in data and data["nextPageToken"] is not None:
            next_page_token = data["nextPageToken"]
        else:
            break

    return issues


def summarize_open_issues(issues):
    open_issue_summary = {
        "recipients": set(),
        "customer_issues": {},
        "issue_count": 0
    }

    for issue in issues:
        fields = issue["fields"]

        assignee = fields["assignee"]["displayName"] if fields["assignee"] is not None else "Unassigned"
        if "fixVersions" in fields:
            release_dates = "\n".join([fix_version["releaseDate"] if "releaseDate" in fix_version else "N/A" for fix_version in fields["fixVersions"]])
        else:
            release_dates = "N/A"

        issue_data = {
            "key": issue["key"],
            "type": fields["issuetype"]["name"],
            "assignee": assignee,
            "summary": fields["summary"],
            "releases": "\n".join([fix_version["name"] for fix_version in fields["fixVersions"]]),
            "release_dates": release_dates,
            "priority": fields["priority"]["name"],
            "status": fields["status"]["name"],
            "issue_link": urljoin(args.jira_url, "browse/{}".format(issue["key"]))
        }

        if "customfield_10232" not in fields or fields["customfield_10232"] is None:
            issue_data["severity"] = "N/A"
        else:
            issue_data["severity"] = fields["customfield_10232"]["value"]

        if fields["assignee"] is not None:
            open_issue_summary["recipients"].add(fields["assignee"]["emailAddress"].replace("@ncr.com", "@ncrvoyix.com"))

        if "customfield_10248" not in fields or fields["customfield_10248"] is None:
            customers = "N/A"
        else:
            customers = "\n".join([originatingCustomer["value"] for originatingCustomer in fields["customfield_10248"]])

        if customers not in open_issue_summary["customer_issues"]:
            open_issue_summary["customer_issues"][customers] = {}
            open_issue_summary["customer_issues"][customers]["issues"] = []

        open_issue_summary["customer_issues"][customers]["issues"].append(issue_data)

    for customers_name, customer_issues in open_issue_summary["customer_issues"].items():
        customer_issues["issue_count"] = len(customer_issues["issues"])
        customer_issues["issues"] = sorted(customer_issues["issues"], key=lambda issue: issue["release_dates"])
        open_issue_summary["issue_count"] = open_issue_summary["issue_count"] + customer_issues["issue_count"]

    return open_issue_summary


def fetch_open_issues_in_old_releases_summary():

    # Get list of all releases (Versions)
    unfinished_releases_list = fetch_unfinished_releases(days_old=60)
    unfinished_releases_ids = [int(unfinished_release["id"]) for unfinished_release in unfinished_releases_list]

    logging.debug("List of all old unfinished releases: {}".format([old_release["name"] for old_release in unfinished_releases_list]))

    # Get list of all open issues in the old releases
    issues = fetch_open_issues(unfinished_releases_ids, None)

    logging.debug("List of all old issues: {}".format([issue["key"] for issue in issues]))

    return summarize_open_issues(issues)


def fetch_open_issues_in_main_releases_summary():

    # Get list of all releases (Versions)
    main_releases_list, main_next_release = fetch_open_main_releases()
    main_releases_ids = [int(main_release["id"]) for main_release in main_releases_list]
    main_next_release_id = int(main_next_release["id"]) if main_next_release is not None else None

    logging.debug("Main Next release: {} = {}".format(main_next_release["name"], main_next_release_id))
    logging.debug("List of all open main releases: {}".format([open_main_release["name"] for open_main_release in main_releases_list]))

    # Get list of all open issues in the old releases
    open_issues_in_main = fetch_open_issues(main_releases_ids, main_next_release_id)

    logging.debug("List of all open issues in main: {}".format([open_issue["key"] for open_issue in open_issues_in_main]))

    return summarize_open_issues(open_issues_in_main)


def fetch_estimated_epics():

    params = {
        "jql": r"project = {} AND issueType IN (Epic) AND originalEstimate > 0 AND status not in (Withdrawn)".format(args.jira_project_key),
        "fields": "key, summary, status, timetracking, aggregatetimespent"
    }

    full_url = urljoin(args.jira_url, "rest/api/3/search/jql")
    get_response = rate_limited_get(full_url, auth=jira_basic_auth, params=params)
    if not get_response.ok:
        logging.error(
            "Failed to get list of all estimated epics for {}! Status: {}, Error(s): {}".format(args.jira_project_key, get_response.status_code, get_response.text))
        return []

    data = get_response.json()
    if "issues" not in data:
        logging.error(
            "Invalid/Unexpected response while getting list of all estimated epics for {}! Status: {}, Error(s): {}".format(
                args.jira_project_key,
            get_response.status_code, get_response.text))
        return []

    return data["issues"]


def prepare_e2a_for_epic(epic):
    fields = epic["fields"]
    estimate = fields["timetracking"]["originalEstimateSeconds"] / 60 / 60
    actual_issue = fields["timetracking"]["timeSpentSeconds"] / 60 / 60 if "timeSpentSeconds" in fields["timetracking"] else 0.0
    actual_aggregated = fields["aggregatetimespent"] / 60 / 60 if "aggregatetimespent" in fields and fields["aggregatetimespent"] is not None else 0.0

    logging.debug("Actual for Epic {}: Direct = {}, Aggregated = {}".format(epic["key"], actual_issue, actual_aggregated))

    actual = max(actual_issue, actual_aggregated)
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

    worklog_summary = {
        "authors": {},
        "total": 0.0
    }

    worklog_data = fetch_epic_worklog(epic_id)
    for worklog in worklog_data:
        author = resolve_jira_user(worklog["author"]["accountId"])
        time = worklog["timeSpentSeconds"]
        if author not in worklog_summary["authors"]:
            worklog_summary["authors"][author] = time / 60 / 60
        else:
            worklog_summary["authors"][author] += time / 60 / 60

        worklog_summary["total"] += time / 60 / 60

    return worklog_summary


@app.route('/e2a/<epic_id>')
def e2a_summary(epic_id):
    return render_template("worklog_summary.html", e2a=prepare_e2a_for_epic_id(epic_id), ws=prepare_worklog_summary(epic_id))


@app.route('/e2a')
def e2a():
    return render_template("estimates-2-actuals.html", e2a=prepare_e2a())


@app.route('/old-releases')
def old_releases():
    return render_template("open_jira_summary.html", issue_summary=fetch_open_issues_in_old_releases_summary(), page_label="Open Issues in Old Releases")


@app.route('/quality-compliance')
def quality_compliance():
    return render_template("open_jira_summary.html", issue_summary=fetch_open_issues_in_main_releases_summary(), page_label="Open Issues in Main Releases")


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='Controls the pool filtration pump.')
    parser.add_argument("--log-path", dest="log_path", type=str,
                        help="Path to directory where to store log files.", required=False)
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
    parser.add_argument("--port-number", dest="port_number", type=int,
                        help="Port to run the HTTP server on")

    args = parser.parse_args()

    if args.log_path is not None and len(args.log_path) > 0:
        log_path = args.log_path
    else:
        log_path = get_workdir_path()

    if not os.path.exists(log_path):
        os.makedirs(log_path)

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(log_path, "jpt.log"), encoding="utf-8",
                            maxBytes=10485760, backupCount=20),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.DEBUG,
        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")

    jira_basic_auth = HTTPBasicAuth(args.jira_user, args.jira_password)

    jira_user_cache = {}

    app.run(debug=True, host="0.0.0.0", port=args.port_number)

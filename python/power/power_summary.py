import functools
import os
import logging
import traceback
import time

from logging.handlers import RotatingFileHandler
import sys
from pathlib import Path

# date & time stuff
from datetime import datetime, timedelta
from dateutil import tz

# schedule
import schedule

# database
import mariadb


def get_log_path():
    return os.path.join(str(Path.home()), "log/power/")


def create_connection():
    return mariadb.connect(host='localhost', database='power')


def catch_exceptions(cancel_on_failure=False):
    def catch_exceptions_decorator(job_func):
        @functools.wraps(job_func)
        def wrapper(*args, **kwargs):
            try:
                return job_func(*args, **kwargs)
            except:
                logging.error(traceback.format_exc())
                if cancel_on_failure:
                    return schedule.CancelJob

        return wrapper

    return catch_exceptions_decorator


def ensure_summary_table_created(db_connection):
    cur = db_connection.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS `total_power_daily` (
                      `date`        date NOT NULL,
                      `source`      int(11) NOT NULL,
                      `total_power` double DEFAULT NULL,
                      `final`       int(11) DEFAULT NULL,
                      PRIMARY KEY (`date`,`source`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)
    cur.close()
    db_connection.commit()


def setup_database(db_connection):
    ensure_summary_table_created(db_connection)


@catch_exceptions(cancel_on_failure=False)
def summarize_pulses():
    logging.info("Summarizing pulses ...")


if __name__ == '__main__':

    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_log_path(), "power_summary.log"), encoding="utf-8",
                            maxBytes=10485760, backupCount=20),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.DEBUG,
        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")

    logging.info("Setting up the database ...")
    setup_database(create_connection())

    logging.info("Summarizer up and running ... ")

    # Schedule a job to summarize pulses into just daily consumption
    schedule.every(1).days.do(summarize_pulses)

    # Run the job immediately after a startup
    schedule.run_all()

    # And finally, according to a schedule
    while True:
        schedule.run_pending()
        time.sleep(1)

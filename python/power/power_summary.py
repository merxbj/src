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

import mariadb

# schedule
import schedule

# database
import mariadb
db_connection = None


def get_log_path():
    return os.path.join(str(Path.home()), "log/power/")


def create_connection():
    close_connection()

    global db_connection
    db_connection = mariadb.connect(host='localhost', database='power')


def close_connection():
    global db_connection
    if db_connection is not None:
        db_connection.close()
        db_connection = None


def query_db(query, args=(), one=False):
    cur = db_connection.cursor(buffered=True, dictionary=True)
    cur.execute(query, args)
    rv = cur.fetchall()
    cur.close()
    return (rv[0] if rv else None) if one else rv


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


def ensure_summary_table_created():
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


def setup_database():
    ensure_summary_table_created()


def date_range(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)


def get_power_reading_sources():
    sources_raw = query_db("""
                    SELECT * FROM pulse_source
                """)
    return sources_raw


def get_oldest_pulse(source):
    oldest_pulse_raw = query_db("""
                        SELECT timestamp 
                          FROM pulse 
                         WHERE source = ? 
                         ORDER BY timestamp ASC
                         LIMIT 1
                    """, source, one=True)
    return oldest_pulse_raw["timestamp"]

def get_pulse_count_for_date(date, source):
    filter_from = date
    filter_from = filter_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())
    filter_from = filter_from.astimezone(tz.tzutc())

    filter_to = date
    filter_to = filter_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())
    filter_to = filter_to.astimezone(tz.tzutc())

    raw_total_pulses = query_db("""          
          SELECT COUNT(*) AS total_pulses 
            FROM pulse 
           WHERE timestamp BETWEEN ? AND ?
             AND source = ?
    """, (filter_from, filter_to, source), one=True)

    return raw_total_pulses["total_pulses"]


def store_total_power(total, date, source, final):
    final = 1 if final else 0
    cur = db_connection.cursor()
    cur.execute("REPLACE INTO total_power_daily VALUES(?, ?, ?, ?)", (date, source, total, final))
    cur.close()
    db_connection.commit()


def get_total_power_reading(day, source):
    raw_total = query_db("""
                      SELECT * 
                        FROM total_power_daily
                       WHERE date(date) = ?
                         AND source = ?
                """, (day, source), one=True)
    return raw_total


def get_daily_total_readings(day_from, day_to, source, description):

    daily_total_readings = []

    ensure_summary_table_created()

    for day in date_range(day_from, day_to + timedelta(days=1)):
        raw_total = get_total_power_reading(day, source)
        if raw_total is None or raw_total["final"] == 0:
            total = get_pulse_count_for_date(day, source)
            final = datetime.today().date() > day.date()  # if we are calculating for previous date, it's final
            store_total_power(total, day, source, final)
        else:
            total = raw_total["total_power"]

        # calculate kWh from total Wh for a day
        total = total / 1000.0

        daily_total_readings.append({"date": day, "source": description, "total_power": total})

    return daily_total_readings


@catch_exceptions(cancel_on_failure=False)
def summarize_pulses():
    logging.info("Summarizing pulses ...")

    sources = get_power_reading_sources()
    for source in sources:
        oldest_pulse = get_oldest_pulse(source["source"])

        day_from = oldest_pulse
        day_from = day_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())

        day_to = datetime.now() - timedelta(months=3)
        day_to = day_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())

        #daily_total_readings = get_daily_total_readings(day_from, day_to, source["source"], source["description"])



    # for each source
        # get oldest pulse
        # for each date from that pulse up until now - 3 months
            # calculate and store daily total power
            # delete pulses


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

    # Have just one initial connection for the setup - each schedule run will create its own later
    create_connection()
    setup_database()
    close_connection()

    logging.info("Summarizer up and running ... ")

    # Schedule a job to summarize pulses into just daily consumption
    schedule.every(1).days.do(summarize_pulses)

    # Run the job immediately after a startup
    schedule.run_all()

    # And finally, according to a schedule
    while True:
        schedule.run_pending()
        time.sleep(1)

import os
import logging
from logging.handlers import RotatingFileHandler
import sys
from functools import wraps

# date & time stuff
from datetime import timedelta, datetime
from dateutil.relativedelta import relativedelta
import calendar
from dateutil import tz
import time

# web server
from flask import Flask, render_template, g

# database access
from pathlib import Path
import mariadb

# chart rendering
import plotly
import plotly.express as px
import pandas as pd
import json

app = Flask(__name__)


def get_log_path():
    return os.path.join(str(Path.home()), "log/power/")


def create_connection():
    return mariadb.connect(host='localhost', database='power')


def get_db():
    db = getattr(g, '_database', None)
    if db is None:
        db = g._database = create_connection()
    return db


def query_db(query, args=(), one=False):
    cur = get_db().cursor(buffered=True, dictionary=True)
    cur.execute(query, args)
    rv = cur.fetchall()
    cur.close()
    return (rv[0] if rv else None) if one else rv


def date_range(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)


def timed(func):
    """This decorator prints the execution time for the decorated function."""

    @wraps(func)
    def wrapper(*args, **kwargs):
        start = time.perf_counter()
        result = func(*args, **kwargs)
        end = time.perf_counter()
        logging.debug("{} ran in {}s".format(func.__name__, round(end - start, 2)))
        return result

    return wrapper


@app.teardown_appcontext
def close_connection(exception):
    db = getattr(g, '_database', None)
    if db is not None:
        logging.info("Closing the database connection (exception: {})".format(str(exception)))
        db.close()


def calc_power(raw_pulses, source):
    power_readings = []
    last_timestamp = None
    for raw_pulse in raw_pulses:
        if source != raw_pulse["source"]:
            continue

        timestamp = raw_pulse["timestamp"]

        if last_timestamp is not None:
            duration = (timestamp - last_timestamp).total_seconds()
            power = (1 * 60 * 60) / (timestamp - last_timestamp).total_seconds()

            utc = timestamp.replace(tzinfo=tz.tzutc())
            local = utc.astimezone(tz.tzlocal())

            power_readings.append({"timestamp": local, "power": power, "duration": duration, "source": source})

        last_timestamp = timestamp

    power_readings.reverse()
    return power_readings


def calc_power_with_resolution(raw_pulses, source, resolution: timedelta):
    power_readings = []

    # for calculation of avg over a 'resolution' time delta
    current_delta = 0.0
    total_power = 0.0

    last_timestamp = None
    for raw_pulse in raw_pulses:
        if source != raw_pulse["source"]:
            continue

        timestamp = raw_pulse["timestamp"]

        if last_timestamp is not None:
            duration = (timestamp - last_timestamp).total_seconds()
            power = (1 * 60 * 60) / (timestamp - last_timestamp).total_seconds()

            total_power += power * duration
            current_delta += duration

            if current_delta >= resolution.total_seconds():
                utc = timestamp.replace(tzinfo=tz.tzutc())
                local = utc.astimezone(tz.tzlocal())

                power_readings.append({"timestamp": local,
                                       "power": total_power / current_delta,
                                       "duration": current_delta,
                                       "source": source})

                current_delta = 0.0
                total_power = 0.0

        last_timestamp = timestamp

    if total_power != 0.0 and current_delta != 0.0:
        utc = last_timestamp.replace(tzinfo=tz.tzutc())
        local = utc.astimezone(tz.tzlocal())

        power_readings.append({"timestamp": local,
                               "power": total_power / current_delta,
                               "duration": current_delta,
                               "source": source})

    power_readings.reverse()
    return power_readings


def get_power_readings_for_date(date, source):
    filter_from = date
    filter_from = filter_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())
    filter_from = filter_from.astimezone(tz.tzutc())

    filter_to = date
    filter_to = filter_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())
    filter_from = filter_from.astimezone(tz.tzutc())

    raw_pulses = query_db("""
          SELECT PULSE.* 
            FROM (
                  -- get the last pulse from the day before
                  SELECT *
                    FROM (
                          SELECT *
                            FROM pulse
                           WHERE timestamp BETWEEN SUBDATE(?, 1) 
                                                         AND SUBDATE(?, 1)
                             AND source = ?
                        ORDER BY timestamp DESC
                           LIMIT 1
                    ) AS PREVIOUS_DAY
                  
                   UNION
                  
                  -- get all pulses from the day
                  SELECT *
                    FROM (
                          SELECT * 
                            FROM pulse 
                           WHERE timestamp BETWEEN ?
                                                         AND ?
                             AND source = ?
                    ) AS DAY
            ) PULSE
        ORDER BY timestamp ASC
    """, (filter_from, filter_to, source, filter_from, filter_to, source))

    return calc_power_with_resolution(raw_pulses, source, timedelta(seconds=60))


def ensure_power_over_day_cache_table_created():
    con = get_db()

    cur = con.cursor()
    cur.execute("""
            CREATE TABLE IF NOT EXISTS `power_over_day_cache` (
              `date`     date NOT NULL,
              `fig_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`fig_json`)),
              PRIMARY KEY (`date`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)
    cur.close()
    con.commit()


def get_cached_fig_json(date):
    ensure_power_over_day_cache_table_created()

    fig_json_raw = query_db("""
                      SELECT fig_json 
                        FROM power_over_day_cache
                       WHERE date = ?
                """, (date,), one=True)

    return fig_json_raw["fig_json"] if fig_json_raw is not None else fig_json_raw


def cache_fig_json(date, fig_json):
    cur = get_db().cursor()
    cur.execute("REPLACE INTO power_over_day_cache VALUES(?,?)", (date, fig_json))
    cur.close()
    get_db().commit()

    logging.info("Power over day chart for {:%A, %x} stored into the cache!".format(date))


def get_power_reading_sources():
    sources_raw = query_db("""
                    SELECT * FROM pulse_source
                """)
    return sources_raw


@app.route('/pod/<date>')
@timed
def render_power_over_day_chart(date):
    date = datetime.fromisoformat(date)
    cached_fig_json = get_cached_fig_json(date)
    if cached_fig_json is not None:
        logging.info("Power over day chart for {:%A, %x} found in the cache!".format(date))
        return cached_fig_json

    df = pd.DataFrame()

    sources = get_power_reading_sources()
    for source in sources:
        power_readings = get_power_readings_for_date(date, source=source["source"])
        df = df.append(power_readings)

    fig = px.line(df,
                  x="timestamp",
                  y="power",
                  color="source",
                  title="Power consumption on {:%A, %x}:".format(date),
                  labels={"timestamp": "Time of Day", "power": "Power (W)", "source": "Consumer"})

    for data in fig.data:
        source = next((source for source in sources if str(source["source"]) == data.name), None)
        if source is not None:
            data.name = source["description"]
            data.hovertemplate = data.hovertemplate.replace("Consumer={}".format(source["source"]),
                                                            "Consumer={}".format(source["description"]))

    fig_json = json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)

    if datetime.today().date() > date.date():
        cache_fig_json(date, fig_json)

    return fig_json


def get_latest_pulses(count, source):
    raw_pulses = query_db("""
                  SELECT * 
                    FROM pulse
                   WHERE source = ?
                ORDER BY timestamp DESC
                LIMIT ?
            """, (source, count))
    raw_pulses.reverse()
    return raw_pulses


def get_latest_power_reading(source):
    raw_pulses = get_latest_pulses(count=2, source=source)

    return calc_power(raw_pulses, source)[0]


@app.route('/pb')
@timed
def render_power_bar():
    latest_power_readings = []

    sources = get_power_reading_sources()
    for source in sources:
        latest_power_reading = get_latest_power_reading(source["source"])
        latest_power_readings.append({"source": source["description"], "power": latest_power_reading["power"]})

    df = pd.DataFrame.from_records(latest_power_readings)
    fig = px.bar(df,
                 x="source",
                 y="power",
                 color="source",
                 title="Current Power Level",
                 range_y=[0, 5000],
                 width=500,
                 labels={"source": "Consumer", "power": "Power (W)"})
    fig.update_xaxes(showticklabels=False)
    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


def ensure_summary_table_created():
    con = get_db()

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS `total_power_daily` (
                      `date`        date NOT NULL,
                      `source`      int(11) NOT NULL,
                      `total_power` double DEFAULT NULL,
                      `final`       int(11) DEFAULT NULL,
                      PRIMARY KEY (`date`,`source`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)
    cur.close()
    con.commit()


def get_total_power_reading(day, source):
    raw_total = query_db("""
                      SELECT * 
                        FROM total_power_daily
                       WHERE date(date) = ?
                         AND source = ?
                """, (day, source), one=True)
    return raw_total


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
    cur = get_db().cursor()
    cur.execute("REPLACE INTO total_power_daily VALUES(?, ?, ?, ?)", (date, source, total, final))
    cur.close()
    get_db().commit()


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


@app.route('/pom')
@timed
def render_power_over_month_chart():

    day_from = datetime.now() - timedelta(days=30)
    day_from = day_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())

    day_to = datetime.now()
    day_to = day_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())

    df = pd.DataFrame()

    sources = get_power_reading_sources()
    for source in sources:
        daily_total_readings = get_daily_total_readings(day_from, day_to, source["source"], source["description"])
        df = df.append(daily_total_readings)

    fig = px.bar(df,
                 x="date",
                 y="total_power",
                 color="source",
                 barmode="relative",
                 title="Power Totals over last 30 days",
                 labels={"date": "Date", "total_power": "Power (kWh)", "source": "Consumer"})

    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


def ensure_monthly_summary_table_created():
    con = get_db()

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS `total_power_monthly` (
                      `year`        smallint NOT NULL,
                      `month`       tinyint NOT NULL,
                      `source`      int(11) NOT NULL,
                      `total_power` double DEFAULT NULL,
                      `final`       int(11) DEFAULT NULL,
                      PRIMARY KEY (`year`,`month`,`source`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
                """)
    cur.close()
    con.commit()


def get_monthly_total_power(year, month, source):
    raw_total = query_db("""
                      SELECT * 
                        FROM total_power_monthly
                       WHERE year = ?
                         AND month = ?
                         AND source = ?
                """, (year, month, source), one=True)
    return raw_total


def store_monthly_total_power(total, year, month, source, final):
    final = 1 if final else 0
    cur = get_db().cursor()
    cur.execute("REPLACE INTO total_power_monthly VALUES(?, ?, ?, ?, ?)", (year, month, source, total, final))
    cur.close()
    get_db().commit()


def get_monthly_total_readings(day_from, day_to, source, description):

    monthly_total_readings = []

    ensure_monthly_summary_table_created()

    rd = relativedelta(day_to, day_from)
    for month_delta in range(rd.years * 12 + rd.months):
        current_month = day_to - relativedelta(months=month_delta)

        raw_monthly_total = get_monthly_total_power(current_month.year, current_month.month, source)
        if raw_monthly_total is None or raw_monthly_total["final"] == 0:
            fwd, days_in_month = calendar.monthrange(current_month.year, current_month.month)
            daily_total_readings = get_daily_total_readings(current_month.replace(day=1),
                                                            current_month.replace(day=days_in_month),
                                                            source,
                                                            description)
            monthly_total = sum(dtr["total_power"] for dtr in daily_total_readings)
            final = datetime.today().month > current_month.month  # if we are calculating for previous month, it's final
            store_monthly_total_power(monthly_total, current_month.year, current_month.month, source, final)
        else:
            monthly_total = raw_monthly_total["total_power"]

        monthly_total_readings.append({"month": "{} {}".format(current_month.strftime("%b"), current_month.year),
                                       "source": description,
                                       "total_power": monthly_total})

    monthly_total_readings.reverse()
    return monthly_total_readings


@app.route('/poy')
@timed
def render_power_over_year_chart():

    day_from = datetime.now() - relativedelta(months=12)
    day_from = day_from.replace(day=1, tzinfo=tz.tzlocal())

    day_to = datetime.now()
    day_to = day_to.replace(day=1, tzinfo=tz.tzlocal())

    df = pd.DataFrame()

    sources = get_power_reading_sources()
    for source in sources:
        monthly_total_readings = get_monthly_total_readings(day_from, day_to, source["source"], source["description"])
        df = df.append(monthly_total_readings)

    fig = px.bar(df,
                 x="month",
                 y="total_power",
                 color="source",
                 barmode="relative",
                 title="Power Totals over last 12 months",
                 labels={"month": "Month", "total_power": "Power (kWh)", "source": "Consumer"})

    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


def get_available_dates():
    available_dates_raw = query_db("""
          SELECT DISTINCT available_date AS date
            FROM available_date
        ORDER BY available_date
    """)

    available_dates = []
    for row in available_dates_raw:
        available_dates.append(row["date"])

    # if "today" wasn't generated yet, add it manually - it still has to be available on the page
    if datetime.today().date() not in available_dates:
        available_dates.append(datetime.today().date())

    available_dates.reverse()
    return available_dates


def get_years_months_and_days(available_dates):
    years = {}
    for available_date in available_dates:

        year = available_date.year
        if year not in years:
            years[year] = {}

        month = available_date.strftime("%B")
        if month not in years[year]:
            years[year][month] = []
            
        years[year][month].append(available_date)

    return years


def render_main_page():
    
    return render_template("index.html", years_months_and_days=get_years_months_and_days(get_available_dates()))


@app.route('/power')
def power_today():

    return render_main_page()


if __name__ == "__main__":
    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(handlers=[
        RotatingFileHandler(os.path.join(get_log_path(), "web.log"), encoding="utf-8",
                            maxBytes=10485760, backupCount=20),
        logging.StreamHandler(stream=sys.stdout)
    ],
        level=logging.DEBUG,
        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")

    app.run(debug=True, host="0.0.0.0", port=8081)

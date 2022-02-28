import os
import re
import logging
from functools import wraps

# date & time stuff
from datetime import timedelta, datetime
from dateutil import tz
import time

# web server
from flask import Flask, render_template, g

# database access
from pathlib import Path
import sqlite3

# chart rendering
import plotly
import plotly.express as px
import pandas as pd
import json

app = Flask(__name__)


def get_data_path():
    return os.path.join(str(Path.home()), "data/power/")


def get_log_path():
    return os.path.join(str(Path.home()), "log/power/")


def create_connection():
    return sqlite3.connect(os.path.join(get_data_path(), "power.dat"))


def get_db():
    db = getattr(g, '_database', None)
    if db is None:
        db = g._database = create_connection()
        db.row_factory = sqlite3.Row
    return db


def query_db(query, args=(), one=False):
    cur = get_db().execute(query, args)
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
        db.close()


@timed
def calc_power(raw_pulses, source):
    power_readings = []
    last_timestamp = None
    for raw_pulse in raw_pulses:
        if source != raw_pulse["source"]:
            continue

        timestamp = datetime.fromisoformat(raw_pulse["timestamp"])

        if last_timestamp is not None:
            duration = (timestamp - last_timestamp).total_seconds()
            power = (1 * 60 * 60) / (timestamp - last_timestamp).total_seconds()

            utc = timestamp.replace(tzinfo=tz.tzutc())
            local = utc.astimezone(tz.tzlocal())

            power_readings.append({"timestamp": local, "power": power, "duration": duration, "source": source})

        last_timestamp = timestamp

    power_readings.reverse()
    return power_readings


@timed
def calc_power_with_resolution(raw_pulses, source, resolution: timedelta):
    power_readings = []

    # for calculation of avg over a 'resolution' time delta
    current_delta = 0.0
    total_power = 0.0

    last_timestamp = None
    for raw_pulse in raw_pulses:
        if source != raw_pulse["source"]:
            continue

        timestamp = datetime.fromisoformat(raw_pulse["timestamp"])

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


@timed
def get_power_readings_for_date(date, source):
    filter_from = date
    filter_from = filter_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())

    filter_to = date
    filter_to = filter_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())

    params = {"timestamp_from": filter_from.isoformat(), "timestamp_to": filter_to.isoformat(), "source": source}

    raw_pulses = query_db("""
          SELECT * 
            FROM (
                  -- get the last pulse from the day before
                  SELECT *
                    FROM (
                          SELECT *
                            FROM pulse
                           WHERE datetime(timestamp) BETWEEN datetime(:timestamp_from, '- 1 day') 
                                                         AND datetime(:timestamp_to, '- 1 day')
                             AND source = :source
                        ORDER BY datetime(timestamp) DESC
                           LIMIT 1
                    )
                  
                   UNION
                  
                  -- get all pulses from the day
                  SELECT *
                    FROM (
                          SELECT * 
                            FROM pulse 
                           WHERE datetime(timestamp) BETWEEN datetime(:timestamp_from) 
                                                         AND datetime(:timestamp_to)
                             AND source = :source
                    )
            )
        ORDER BY datetime(timestamp) ASC
    """, params)

    return calc_power_with_resolution(raw_pulses, source, timedelta(seconds=60))


def ensure_power_over_day_cache_table_created():
    con = get_db()

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS power_over_day_cache (
                        date        TEXT,
                        fig_json    TEXT,
                        PRIMARY KEY (date)
                       );
                """)
    cur.close()
    con.commit()


def get_cached_fig_json(date):
    ensure_power_over_day_cache_table_created()

    fig_json_raw = query_db("""
                      SELECT fig_json 
                        FROM power_over_day_cache
                       WHERE date(date) = date(:date)
                """, {"date": date}, one=True)

    return fig_json_raw["fig_json"] if fig_json_raw is not None else fig_json_raw


def cache_fig_json(date, fig_json):
    params = {
        "date": date,
        "fig_json": fig_json
    }
    cur = get_db().cursor()
    cur.execute("INSERT OR REPLACE INTO power_over_day_cache VALUES(:date, :fig_json)", params)
    cur.close()
    get_db().commit()

    logging.info("Power over day chart for {:%A, %x} stored into the cache!".format(date))


def get_power_reading_sources():
    sources_raw = query_db("""
                    SELECT * FROM pulse_source
                """)
    return sources_raw


@timed
def render_power_over_day_chart(date):

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

    fig_json = json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)
    if datetime.today().date() > date.date():
        cache_fig_json(date, fig_json)

    return fig_json


def get_latest_pulses(count, source):
    raw_pulses = query_db("""
                  SELECT * 
                    FROM pulse
                   WHERE source = :source
                ORDER BY datetime(timestamp) DESC
                LIMIT :count
            """, {"count": count, "source": source})
    raw_pulses.reverse()
    return raw_pulses


@timed
def get_latest_power_reading(source):
    raw_pulses = get_latest_pulses(count=2, source=source)

    return calc_power(raw_pulses, source)[0]


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
    cur.execute("""CREATE TABLE IF NOT EXISTS total_power_daily (
                        date        TEXT,
                        source      INTEGER,
                        total_power REAL,
                        final       INTEGER,
                        PRIMARY KEY (date, source)
                       );
                """)
    cur.close()
    con.commit()


def get_total_power_reading(day, source):
    raw_total = query_db("""
                      SELECT * 
                        FROM total_power_daily
                       WHERE date(date) = date(:day)
                         AND source = :source
                """, {"day": day.isoformat(), "source": source}, one=True)
    return raw_total


def get_pulse_count_for_date(date, source):
    filter_from = date
    filter_from = filter_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())

    filter_to = date
    filter_to = filter_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())

    params = {"timestamp_from": filter_from.isoformat(), "timestamp_to": filter_to.isoformat(), "source": source}

    raw_total_pulses = query_db("""          
          SELECT COUNT(*) AS total_pulses 
            FROM pulse 
           WHERE datetime(timestamp) BETWEEN datetime(:timestamp_from) AND datetime(:timestamp_to)
             AND source = :source
    """, params, one=True)

    return raw_total_pulses["total_pulses"]


def store_total_power(total, date, source, final):
    params = {
        "total_power": total,
        "day": date.isoformat(),
        "source": source,
        "final": 1 if final else 0
    }
    cur = get_db().cursor()
    cur.execute("INSERT OR REPLACE INTO total_power_daily VALUES(:day, :source, :total_power, :final)", params)
    cur.close()
    get_db().commit()


@timed
def get_daily_total_readings(day_from, day_to, source, description):

    daily_total_readings = []

    ensure_summary_table_created()

    for day in date_range(day_from, day_to + timedelta(days=1)):
        total = 0.0
        raw_total = get_total_power_reading(day, source)
        if raw_total is None or raw_total["final"] == 0:
            pulse_count = get_pulse_count_for_date(day, source)
            if pulse_count > 0:
                total = pulse_count
                final = datetime.today().date() > day.date()  # if we are calculating for previous date, it's final
                store_total_power(total, day, source, final)
        else:
            total = raw_total["total_power"]

        # calculate kWh from total Wh for a day
        total = total / 1000.0

        daily_total_readings.append({"date": day, "source": description, "total_power": total})

    return daily_total_readings


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
                 barmode="group",
                 title="Power Totals over last 30 days",
                 labels={"date": "Date", "total_power": "Power (kWh)", "source": "Consumer"})

    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


@timed
def get_available_dates():
    available_dates = query_db("""
          SELECT DISTINCT date(timestamp) AS date
            FROM pulse
        ORDER BY date(timestamp) DESC
        LIMIT 7
    """)

    available_dates.reverse()
    return available_dates


def render_main_page(date):

    pod_json = render_power_over_day_chart(date)
    pb_json = render_power_bar()
    pom_json = render_power_over_month_chart()
    available_dates = get_available_dates()

    return render_template("index.html",
                           podJson=pod_json,
                           pbJson=pb_json,
                           pomJson=pom_json,
                           availableDates=available_dates)


@app.route('/power')
def power_today():

    return render_main_page(datetime.now())


@app.route('/power/<day_specifier>')
def power_relative_date(day_specifier):
    if re.match("^[+-]?\d+$", day_specifier) is not None:
        return render_main_page(datetime.now() + timedelta(int(day_specifier)))
    else:
        return render_main_page(datetime.fromisoformat(day_specifier))


if __name__ == "__main__":
    if not os.path.exists(get_log_path()):
        os.makedirs(get_log_path())

    logging.basicConfig(filename=os.path.join(get_log_path(), "web.log"),
                        level=logging.DEBUG,
                        format="%(asctime)s | %(name)s | %(levelname)s | %(message)s")
    app.run(debug=True, host="0.0.0.0", port=8081)
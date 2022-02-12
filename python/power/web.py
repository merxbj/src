import os
import re

# date stuff
from datetime import timedelta, datetime
from dateutil import tz

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


@app.teardown_appcontext
def close_connection(exception):
    db = getattr(g, '_database', None)
    if db is not None:
        db.close()


def calc_power(raw_pulses):
    power_readings = []
    last_timestamp = None
    for raw_pulse in raw_pulses:
        timestamp = datetime.fromisoformat(raw_pulse["timestamp"])

        if last_timestamp is not None:
            duration = (timestamp - last_timestamp).total_seconds()
            power = (1 * 60 * 60) / (timestamp - last_timestamp).total_seconds()

            utc = timestamp.replace(tzinfo=tz.tzutc())
            local = utc.astimezone(tz.tzlocal())

            power_readings.append({"timestamp": local, "power": power, "duration": duration})

        last_timestamp = timestamp

    power_readings.reverse()
    return power_readings


def get_power_readings_for_date(date):
    filter_from = date
    filter_from = filter_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())

    filter_to = date
    filter_to = filter_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())

    params = {"timestamp_from": filter_from.isoformat(), "timestamp_to": filter_to.isoformat()}

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
                    )
            )
        ORDER BY datetime(timestamp) ASC
    """, params)

    return calc_power(raw_pulses)


def render_power_over_day_chart(date):
    power_readings = get_power_readings_for_date(date)
    df = pd.DataFrame.from_records(power_readings)
    fig = px.line(df,
                  x="timestamp",
                  y="power",
                  title="Power consumption on {:%A, %x}:".format(date),
                  labels={"timestamp": "Time of Day", "power": "Power (W)"})
    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


def get_latest_pulses(count):
    raw_pulses = query_db("""
                  SELECT * 
                    FROM pulse
                ORDER BY datetime(timestamp) DESC
                LIMIT :count
            """, {"count": count})
    raw_pulses.reverse()
    return raw_pulses


def get_latest_power_reading():
    raw_pulses = get_latest_pulses(count=2)

    return calc_power(raw_pulses)[0]


def render_power_bar():
    latest_power_reading = get_latest_power_reading()
    df = pd.DataFrame.from_records([latest_power_reading])
    fig = px.bar(df,
                 x="timestamp",
                 y="power",
                 title="Current Power Level",
                 range_y=[0, 5000],
                 width=500,
                 labels={"timestamp": "", "power": "Power"})
    fig.update_xaxes(showticklabels=False)
    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


def get_sample_power_readings():
    raw_pulses = get_latest_pulses(100)
    return calc_power(raw_pulses)


def ensure_summary_table_created():
    con = get_db()

    cur = con.cursor()
    cur.execute("""CREATE TABLE IF NOT EXISTS total_power_daily (
                        date        TEXT,
                        total_power REAL,
                        final       INTEGER,
                        PRIMARY KEY (date)
                       );
                """)
    cur.close()
    con.commit()


def get_total_power_reading(day):
    raw_total = query_db("""
                      SELECT * 
                        FROM total_power_daily
                       WHERE date(date) = date(:day)
                """, {"day": day.isoformat()}, one=True)
    return raw_total


def get_pulse_count_for_date(date):
    filter_from = date
    filter_from = filter_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())

    filter_to = date
    filter_to = filter_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())

    params = {"timestamp_from": filter_from.isoformat(), "timestamp_to": filter_to.isoformat()}

    raw_total_pulses = query_db("""          
          SELECT COUNT(*) AS total_pulses 
            FROM pulse 
           WHERE datetime(timestamp) BETWEEN datetime(:timestamp_from) AND datetime(:timestamp_to)
    """, params, one=True)

    return raw_total_pulses["total_pulses"]


def store_total_power(total, date, final):
    params = {
        "total_power": total,
        "day": date.isoformat(),
        "final": 1 if final else 0
    }
    cur = get_db().cursor()
    cur.execute("INSERT OR REPLACE INTO total_power_daily VALUES(:day, :total_power, :final)", params)
    cur.close()
    get_db().commit()


def get_daily_total_readings(day_from, day_to):

    daily_total_readings = []

    ensure_summary_table_created()

    for day in date_range(day_from, day_to + timedelta(days=1)):
        total = 0.0
        raw_total = get_total_power_reading(day)
        if raw_total is None or raw_total["final"] == 0:
            pulse_count = get_pulse_count_for_date(day)
            if pulse_count > 0:
                total = pulse_count
                final = datetime.today().date() > day.date()  # if we are calculating for previous date, it's final
                store_total_power(total, day, final)
        else:
            total = raw_total["total_power"]

        # calculate kWh from total Wh for a day
        total = total / 1000.0

        daily_total_readings.append({"date": day, "total_power": total})

    return daily_total_readings


def render_power_over_month_chart():

    day_from = datetime.now() - timedelta(days=30)
    day_from = day_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())

    day_to = datetime.now()
    day_to = day_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())

    daily_total_readings = get_daily_total_readings(day_from, day_to)
    df = pd.DataFrame.from_records(daily_total_readings)
    fig = px.bar(df,
                 x="date",
                 y="total_power",
                 title="Power Totals over last 30 days",
                 labels={"date": "Date", "total_power": "Power (kWh)"})

    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


def render_main_page(date):

    pod_json = render_power_over_day_chart(date)
    pb_json = render_power_bar()
    pom_json = render_power_over_month_chart()
    sample_power_readings = get_sample_power_readings()

    return render_template("index.html",
                           samplePowerReadings=sample_power_readings,
                           podJson=pod_json,
                           pbJson=pb_json,
                           pomJson=pom_json)


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
    app.run(debug=True, host="0.0.0.0", port=8081)
import datetime
import json
import sys

from dateutil import tz
import os
from pathlib import Path
from flask import Flask, render_template, g
import sqlite3
import plotly
import plotly.express as px
import pandas as pd

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


@app.teardown_appcontext
def close_connection(exception):
    db = getattr(g, '_database', None)
    if db is not None:
        db.close()


def calc_power(raw_pulses):
    power_readings = []
    last_timestamp = None
    for raw_pulse in raw_pulses:
        timestamp = datetime.datetime.fromisoformat(raw_pulse["timestamp"])

        if last_timestamp is None:
            duration = 0
            power = 0
        else:
            duration = (timestamp - last_timestamp).total_seconds()
            power = (1 * 60 * 60) / (timestamp - last_timestamp).total_seconds()

        utc = timestamp.replace(tzinfo=tz.tzutc())
        local = utc.astimezone(tz.tzlocal())

        power_readings.append({"timestamp": local, "power": power, "duration": duration})

        last_timestamp = timestamp

    if len(power_readings) == 0:
        power_readings.append({"timestamp": datetime.datetime.utcnow().replace(tzinfo=tz.tzutc()), "power": 0, "duration": 0})

    power_readings.reverse()
    return power_readings


def get_power_readings_for_today():
    filter_from = datetime.datetime.now()
    filter_from = filter_from.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=tz.tzlocal())

    filter_to = datetime.datetime.now()
    filter_to = filter_to.replace(hour=23, minute=59, second=59, microsecond=999999, tzinfo=tz.tzlocal())

    params = {"timestamp_from": filter_from.isoformat(), "timestamp_to": filter_to.isoformat()}

    raw_pulses = query_db("""
          SELECT * 
            FROM pulse 
           WHERE datetime(timestamp) BETWEEN datetime(:timestamp_from) AND datetime(:timestamp_to)
        ORDER BY datetime(timestamp) ASC
    """, params)

    return calc_power(raw_pulses)


def render_power_over_day_chart():
    power_readings = get_power_readings_for_today()
    df = pd.DataFrame.from_records(power_readings)
    fig = px.line(df,
                  x="timestamp",
                  y="power",
                  title="Power consumption on {:%A, %x}:".format(datetime.datetime.now()),
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


@app.route('/')
def index():

    pod_json = render_power_over_day_chart()
    pb_json = render_power_bar()
    sample_power_readings = get_sample_power_readings()

    return render_template("index.html", samplePowerReadings=sample_power_readings, podJson=pod_json, pbJson=pb_json)


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=8081)
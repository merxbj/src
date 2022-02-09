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


def calc_pulses(raw_pulses):
    pulses = []
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

        pulses.append({"timestamp": local, "power": power, "duration": duration})

        last_timestamp = timestamp

    if len(pulses) == 0:
        pulses.append({"timestamp": datetime.datetime.utcnow().replace(tzinfo=tz.tzutc()), "power": 0, "duration": 0})

    pulses.reverse()
    return pulses


def render_power_over_day_chart(pulses):
    df = pd.DataFrame.from_records(pulses)
    fig = px.line(df,
                  x="timestamp",
                  y="power",
                  title="Power consumption on {:%A, %x}:".format(datetime.datetime.now()),
                  labels={"timestamp": "Time of Day", "power": "Power (W)"})
    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


def render_power_bar(last_pulse):
    df = pd.DataFrame.from_records([last_pulse])
    fig = px.bar(df,
                 x="timestamp",
                 y="power",
                 title="Current Power Level",
                 range_y=[0, 5000],
                 width=500,
                 labels={"timestamp": "", "power": "Power"})
    fig.update_xaxes(showticklabels=False)
    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


@app.route('/')
def index():

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

    pulses = calc_pulses(raw_pulses)

    pod_json = render_power_over_day_chart(pulses)
    pb_json = render_power_bar(pulses[0])

    now = datetime.datetime.utcnow().replace(tzinfo=tz.tzutc())
    time_limited_pulses = list(filter(lambda pulse: (now - pulse["timestamp"]).total_seconds() <= 300, pulses))
    if len(time_limited_pulses) == 0:
        time_limited_pulses = pulses[0:5]

    return render_template("index.html", pulses=time_limited_pulses, podJson=pod_json, pbJson=pb_json)


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=8081)
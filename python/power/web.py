import datetime
import json

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


def calc_ticks(raw_ticks):
    ticks = []
    last_timestamp = None
    for raw_tick in raw_ticks:
        timestamp = datetime.datetime(
            year=raw_tick["year"],
            month=raw_tick["month"],
            day=raw_tick["day"],
            hour=raw_tick["hour"],
            minute=raw_tick["minute"],
            second=raw_tick["second"],
            microsecond=int(raw_tick["millis"]) * 1000)

        if last_timestamp is None:
            duration = 0
            power = 0
        else:
            duration = (timestamp - last_timestamp).total_seconds()
            power = (1 * 60 * 60) / (timestamp - last_timestamp).total_seconds()

        from_zone = tz.tzutc()
        to_zone = tz.tzlocal()
        utc = timestamp.replace(tzinfo=from_zone)
        local = utc.astimezone(to_zone)

        ticks.append({"timestamp": local, "power": power, "duration": duration})

        last_timestamp = timestamp

    ticks.reverse()
    return ticks


def render_power_over_day_chart(ticks):
    df = pd.DataFrame.from_records(ticks)
    fig = px.line(df,
                  x="timestamp",
                  y="power",
                  title="Power consumption on {:%A, %x}:".format(datetime.datetime.now()),
                  labels={"timestamp": "Time of Day", "power": "Power (W)"})
    return json.dumps(fig, cls=plotly.utils.PlotlyJSONEncoder)


def render_power_bar(last_tick):
    df = pd.DataFrame.from_records([last_tick])
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
    now = datetime.datetime.utcnow().replace(tzinfo=tz.tzutc())
    params = {"year": now.year, "month": now.month, "day": now.day}
    raw_ticks = query_db("""
          SELECT * 
            FROM ticks 
           WHERE year = :year
             AND month = :month
             AND day = :day
        ORDER BY hour ASC, minute ASC, second ASC, millis ASC
    """, params)

    ticks = calc_ticks(raw_ticks)

    pod_json = render_power_over_day_chart(ticks)
    pb_json = render_power_bar(ticks[0])

    time_limited_ticks = list(filter(lambda tick: (now - tick["timestamp"]).total_seconds() <= 60, ticks))
    if len(time_limited_ticks) == 0:
        time_limited_ticks = ticks[0:5]

    return render_template("index.html", ticks=time_limited_ticks, podJson=pod_json, pbJson=pb_json)


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=8081)
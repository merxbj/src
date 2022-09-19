# one time conversion script from the old structure to the new structure

import datetime
import os
from dateutil import tz
import sqlite3
from pathlib import Path


def get_db():
    db = create_connection()
    db.row_factory = sqlite3.Row
    return db


def query_db(query, args=(), one=False):
    cur = get_db().execute(query, args)
    rv = cur.fetchall()
    cur.close()
    return (rv[0] if rv else None) if one else rv


def get_data_path():
    return os.path.join(str(Path.home()), "data/power/")


def create_connection():
    return sqlite3.connect(os.path.join(get_data_path(), "power.dat"))


if __name__ == '__main__':
    con = get_db()
    ticks = query_db("SELECT * FROM ticks")
    for raw_tick in ticks:
        timestamp = datetime.datetime(
            year=raw_tick["year"],
            month=raw_tick["month"],
            day=raw_tick["day"],
            hour=raw_tick["hour"],
            minute=raw_tick["minute"],
            second=raw_tick["second"],
            microsecond=int(raw_tick["millis"]) * 1000,
            tzinfo=tz.tzutc())

        params = {
            "source": 23,
            "timestamp": timestamp.isoformat()
        }

        print("Converting tick to pulse: {} : {}".format(23, timestamp.isoformat()))

        cur = con.cursor()
        cur.execute("INSERT INTO pulse VALUES(:source, :timestamp)", params)
        cur.close()
    con.commit()


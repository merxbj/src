# one time conversion script from the old structure to the new structure

import datetime
import os
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
    raw_pulses = query_db("SELECT * FROM pulse WHERE datetime(timestamp) BETWEEN datetime('2022-02-09T00:18:24.455180+00:00') AND datetime('2022-02-09T06:49:47.378521+00:00')")
    for raw_pulse in raw_pulses:
        timestamp = datetime.datetime.fromisoformat(raw_pulse["timestamp"])
        timestamp = timestamp + datetime.timedelta(1)
        params = {
            "source": 21,
            "timestamp": timestamp.isoformat()
        }

        print("Patching hole in pulses with: {} : {}".format(21, timestamp.isoformat()))

        cur = con.cursor()
        cur.execute("INSERT INTO pulse VALUES(:source, :timestamp)", params)
        cur.close()
    con.commit()


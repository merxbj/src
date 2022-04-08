import datetime
import os
import sqlite3
import mariadb
from pathlib import Path


def get_mariadb_db():
    return mariadb.connect(host='localhost', database='power')


def get_sqlite_db():
    db = create_sqlite_connection()
    db.row_factory = sqlite3.Row
    return db


def query_sqlite_db(query, args=(), one=False):
    cur = get_sqlite_db().execute(query, args)
    rv = cur.fetchall()
    cur.close()
    return (rv[0] if rv else None) if one else rv


def create_sqlite_connection():
    root = os.path.join(str(Path.home()), "data/power/")
    return sqlite3.connect(os.path.join(root, "power.dat"))


def import_data(to_db: mariadb, table, columns):
    from_table = query_sqlite_db(f"SELECT * FROM {table}")
    cursor = to_db.cursor()
    param_placeholders = '?,' * (len(columns) - 1)

    for row in from_table:
        data = []
        for column in columns:
            formated_cell = datetime.datetime.fromisoformat(row[column]).strftime('%Y-%m-%dT%H:%M:%S.%f') if (
                        column == "timestamp" or column == "date") else row[column]
            data.append(formated_cell)

        cursor.execute(f"INSERT INTO {table} VALUES ({param_placeholders}?)", data)

    to_db.commit()
    cursor.close()


if __name__ == '__main__':
    maria_db = get_mariadb_db()

    import_data(maria_db, "power_over_day_cache", ["date", "fig_json"])
    import_data(maria_db, "pulse", ["source", "timestamp"])
    # Not Needed: import_data(maria_db, "pulse_source", ["source", "description"])
    import_data(maria_db, "total_power_daily", ["date", "source", "total_power", "final"])

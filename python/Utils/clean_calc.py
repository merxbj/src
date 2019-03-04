import os

from icalendar import Calendar
import humanize

path = r"c:\temp\outlook\Work From Home Calendar.ics"
with open(path) as f:
    calendar = Calendar().from_ical(f.read())

total_size = 0
for component in calendar.walk():
    if component.name == "VEVENT":
        if "X-ALT-DESC" in component:
            print("Removed X-ALT-DESC of size {} from Event with Subject {}".format(len(component["X-ALT-DESC"]), component["SUMMARY"]))
            total_size += len(component["X-ALT-DESC"])
        component["X-ALT-DESC"] = ""

percent = (total_size / os.path.getsize(path)) * 100.0

print()
print("Cleaned up {0} worth of space, which is {1:.0f}% of the original size! FU Outlook!".format(humanize.naturalsize(total_size), percent))

with open(r"c:\temp\outlook\WFH_Clean.ics", mode="wb") as out:
    out.write(calendar.to_ical())

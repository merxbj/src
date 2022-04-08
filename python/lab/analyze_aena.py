import io, os
from os import path
import re

unmapped_items = {}

for root, dirs, files in os.walk(r"c:\temp\Repsol\AENA\Pilot\Incorrect Mapping"):
    files = [file for file in files if file.lower().endswith('txt')]

    for file in files:

        # example: 20220303003043232811001-.txt -> (20220303)(003043)(2328)(11001)-().txt
        match = re.match(r"^(\d{8})(\d{6})(\d+)(\d{5})-(.*)\.txt$", file)
        date, time, sequence, file_type, suffix = match.groups()

        if file_type != "11004":
            continue

        file_path = path.join(root, file)

        with io.open(file_path, "r", encoding="windows-1252") as f:
            lines = f.readlines()

        for line in lines:
            if line.startswith("11004"):
                pass
            elif line.startswith("5"):
                fields = line.split("|")
                plu = fields[1]
                description = fields[2]
                family = fields[3]
                subfamily = fields[4]
                if not family.isdigit() or (int(family) == 0) or not subfamily.isdigit() or (int(subfamily) == 0):
                    unmapped_items[plu] = description
                    #print("Unmapped item: {}:{} in {}".format(plu, description, file_path))

for plu, desc in unmapped_items.items():
    print("{}, \"{}\"".format(plu, desc))
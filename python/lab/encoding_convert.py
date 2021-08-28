import io, os

path = os.path.expanduser(r"~/Documents/credit.csv")

with open(path, encoding="iso-8859-2") as in_file:
    target = path[:-4] + "conv.csv"
    with open(target, encoding="utf-8", mode="w") as out_file:
        out_file.writelines(in_file.readlines())

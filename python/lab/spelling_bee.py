import json
import re

with open(r"/Users/jaroslavlek/Downloads/words_dictionary.json") as f:
    words = json.load(f)

regexp = re.compile("^[bcilame]*b[bcilame]+$")

valid_words = list(filter(lambda word: len(word) > 3, words.keys()))

valid_words = list(filter(lambda word: regexp.match(word), valid_words))

print("Found {} valid words!".format(len(valid_words)))
for word in sorted(valid_words, key=len):
    print(word)


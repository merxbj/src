import sys
import os
import re

backlog = []


def main(argv):
    source = r'f:\Family Fotky'
    print('Source path: {}'.format(source))

    extension = re.compile(r'^.*\..+_\d{3}$')

    for root, dirs, files in os.walk(source):
        duplicates = [duplicate for duplicate in files if extension.match(duplicate.lower())]
        for duplicate in duplicates:
            duplicate_path = os.path.join(root, duplicate)
            orig_path = os.path.join(root, duplicate[:-4])
            if os.path.getsize(duplicate_path) == os.path.getsize(orig_path):
                print('Safe to delete {}. Original is the same'.format(duplicate_path))
                os.remove(duplicate_path)
            else:
                new_duplicate_name = duplicate[:-8] + duplicate_path[-4:] + duplicate_path[-8:-4]
                print('Will rename {} to {}. Original is different.'.format(duplicate, new_duplicate_name))
                os.rename(duplicate_path, os.path.join(root, new_duplicate_name))


if __name__ == '__main__':
    main(sys.argv)

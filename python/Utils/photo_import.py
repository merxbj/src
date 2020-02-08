import datetime
import sys
import os
import shutil

import exifread

backlog = []


def get_capture_date(jpg_path):
    with open(jpg_path, 'rb') as jpg_file:
        tags = exifread.process_file(jpg_file)
        if 'EXIF DateTimeOriginal' in tags:
            capture_time = tags['EXIF DateTimeOriginal'].values
        elif 'Image DateTime' in tags:
            capture_time = tags['Image DateTime'].values
        else:
            capture_time = None

    try:
        capture_date = datetime.datetime.strptime(capture_time[:10], '%Y:%m:%d') if capture_time is not None else None
    except:
        capture_date = None

    return capture_date


def import_picture(source_path, file_name, capture_date, destination_root):
    try:
        destination_final_dir = '{:04}-{:02}-{:02}'.format(capture_date.year, capture_date.month, capture_date.day)
        destination_subdir = os.path.join(str(capture_date.year), destination_final_dir)
        destination_path = os.path.join(destination_root, destination_subdir, file_name)

        os.makedirs(os.path.join(destination_root, destination_subdir), exist_ok=True)

        counter = 0
        while os.path.isfile(destination_path):
            if os.path.getsize(destination_path) == os.path.getsize(source_path):
                print('WARNING: {} is already in destination. Skipping.'.format(file_name))
                destination_path = ''
            else:
                counter += 1
                new_file_name = '{}_{:03}'.format(file_name, counter)
                destination_path = os.path.join(destination_root, destination_subdir, new_file_name)

        if destination_path != '':
            shutil.copy(source_path, destination_path)

        print('SUCCESS: {} -> {}'.format(source_path, destination_path))
    except:
        append_to_backlog(source_path, file_name, 'Failed to move to destination.')


def append_to_backlog(jpg_path, jpg, reason):
    backlog.append([jpg_path, jpg])
    print('ERROR: {} moved to backlog. Size = {}. Reason = {}'.format(jpg_path, len(backlog), reason), file=sys.stderr)


def main(argv):
    source = r'D:\Photography\2019'
    destination = r'F:\Family Fotky'
    print('Source path: {}'.format(source))
    print('Destination path: {}'.format(destination))

    for root, dirs, files in os.walk(source):
        jpgs = [jpg for jpg in files if jpg.lower().endswith('jpg')]
        for jpg in jpgs:
            jpg_path = os.path.join(root, jpg)
            capture_date = get_capture_date(jpg_path)

            if capture_date is None:
                append_to_backlog(jpg_path, jpg, 'Failed to get capture time.')
            else:
                import_picture(jpg_path, jpg, capture_date, destination)

    for file_path, file in backlog:
        dest_file = os.path.join(destination, '0000', file)
        print('WARNING: {} -> {}'.format(file_path, dest_file))
        shutil.copy(file_path, dest_file)


if __name__ == '__main__':
    main(sys.argv)

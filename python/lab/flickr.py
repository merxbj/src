import json
import shutil
from os import path, makedirs

with open(r"c:\temp\photo\data\albums.json", "r") as albums_file:
    albums = json.load(albums_file)
    albums = albums['albums']

for album in albums:

    print('INFO: Processing album {} with {} photos in it.'.format(album['title'], album['photo_count']))

    album_path = path.join(r"c:\temp\photo", album['title'])
    makedirs(album_path, exist_ok=True)

    expected_photo_count = int(album['photo_count'])
    photo_count = 0

    for photo_ref in album['photos']:

        photo_count += 1

        photo_data_file_path = path.join(r"c:\temp\photo\data", "photo_{}.json".format(photo_ref))
        with open(photo_data_file_path, "r") as photo_data_file:
            photo_data = json.load(photo_data_file)

        photo_file_name = "{}_{}_o.jpg".format(photo_data['name'].replace(".",""), photo_data["id"])
        photo_file_path = path.join(r"c:\temp\photo", photo_file_name)
        if path.isfile(photo_file_path):
            photo_file_dest_name = photo_data['name']

            if path.isfile(path.join(album_path, photo_file_dest_name)):
                photo_file_dest_name = photo_file_name
                print('\tWARNING: {} already exists in the album! Using {} instead.'.format(photo_data['name'], photo_file_name))

            shutil.move(photo_file_path, path.join(album_path, photo_file_dest_name))
            print('\t\tINFO: {} -> {}'.format(photo_file_name, photo_file_dest_name))

            for comment in photo_data['comments']:
                print('\t\t\t    {}:{}'.format(comment['date'], comment['comment']))

        else:
            print('\tERROR: {} doesnt exist!'.format(photo_file_path))

    if photo_count != expected_photo_count:
        print('\tWARNING: Organized {} out of {} expected photos.'.format(photo_count, expected_photo_count))
    else:
        print('\tINFO: Successfully organized {} photos.'.format(photo_count))
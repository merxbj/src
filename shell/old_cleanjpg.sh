#!/bin/bash
 
root=~/Pictures/Fotky/2013
 
for f in $(find $root -name '*.CR2');
do
  fname=`echo $f | cut -d\. -f1`
  if [ -e $fname.jpg ]
  then
    echo Removing $fname.jpg ...
    rm $fname.jpg
  elif [ -e $fname.JPG ]
  then
    echo Removing $fname.JPG ...
    rm $fname.JPG
  elif [ -e $fname.jpeg ]
  then
    echo Removing $fname.jpeg ...
    rm $fname.jpeg
  elif [ -e $fname.JPEG ]
  then
    echo Removing $fname.JPEG ...
    rm $fname.JPEG
  fi
done
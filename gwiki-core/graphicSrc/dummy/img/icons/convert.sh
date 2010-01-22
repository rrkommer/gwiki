#!/bin/bash

for png in `ls *.png`
do
convert -thumbnail 16x16 $png $png
done
#!/bin/bash

rm *.properties

for var in `ls *`
do
	echo "TYPE=attachment">${var}Settings.properties
	echo "AUTH_VIEW=GWIKI_PUBLIC">>${var}Settings.properties
	echo "WIKIMETATEMPLATE=admin/templates/FileWikiPageMetaTemplate">>${var}Settings.properties
done
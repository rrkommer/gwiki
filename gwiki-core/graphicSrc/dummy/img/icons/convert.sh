#!/bin/bash

for var in `ls *`
do
	
	echo "TYPE=attachment">>${var}Settings.properties
	echo "AUTH_VIEW=GWIKI_PUBLIC">>
	echo "WIKIMETATEMPLATE=admin/templates/FileWikiPageMetaTemplate">>
	
	
	
done
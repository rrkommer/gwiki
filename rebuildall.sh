GV=0.9.0-SNAPSHOT
MVN=/cygdrive/c/programr/apache-maven-2.2.1/bin/mvn
PRC="gwiki-parent gwiki-genome-dependencies gwiki-gdbfs"
PLUGINS="gwiki-admintools gwiki-s5slideshow"

ADDPLUGINS="gwiki-blog gwiki-forum gwiki-fssvn gwiki-keywordsmarttags org.apache.pdfbox org.apache.poi org.apache.httpcomponents gwiki-msotextextractor gwiki-vfolder gwiki-pdftextextractor gwiki-rte-myspell gwiki-sampleplugin gwiki-scheduler gwiki-pagelifecycle gwiki-pagetemplates gwiki-style-GracefulUndressed gwiki-style-mmLabs gwiki-feed gwiki-confluenceimporter"
# gwiki-style-ProjectForge 
DEPLOYCMD=install
INSTALLCMD="install"
STARTSTANDALONE=false

for pm in $PRC
do 
	cd $pm;
	$MVN $DEPLOYCMD;
	cd ..;
done;

cd gwiki
$MVN install
cd ..

cd plugins
for pm in $PLUGINS
do
	cd $pm;
	$MVN $INSTALLCMD;
	cp target/*.zip ../../gwiki/src/main/resources_zip/gwiki/admin/plugins/
	cd ..;
done;

for pm in $ADDPLUGINS
do
	cd $pm;
	$MVN $INSTALLCMD;
	cd ..;
done;

cd ../gwiki
$MVN $DEPLOYCMD
cd ../gwiki-standalone
$MVN $DEPLOYCMD
cd ../gwiki-webapp
$MVN $DEPLOYCMD
cd ../gwiki-wicket
$MVN $DEPLOYCMD
cd ../temp

if [ $STARTSTANDALONE == 'true' ]
then
	
rm -rf gwt
mkdir gwt
cd gwt
cp ../../gwiki-standalone/target/assembly/gwiki-standalone-$GV-bin.zip .
unzip gwiki-standalone-$GV-bin.zip
cd gwiki-standalone-$GV
cmd gwikiweb.cmd
fi

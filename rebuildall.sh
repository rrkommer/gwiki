GV=1.0.1
MVN=/cygdrive/c/programr/apache-maven-3.1.0/bin/mvn
#MVN=mvn
PRC="gwiki-parent gwiki-genome-dependencies gwiki-gdbfs gwiki"
PLUGINS="gwiki-plugin-parent gwiki-admintools gwiki-s5slideshow gwiki-wikilinks gwiki-feed gwiki-blog gwiki-forum gwiki-fssvn gwiki-keywordsmarttags org.apache.pdfbox org.apache.poi org.apache.httpcomponents gwiki-msotextextractor gwiki-vfolder gwiki-pdftextextractor gwiki-rte-myspell gwiki-sampleplugin gwiki-scheduler gwiki-confluenceimporter"
BETAPLUGINS="gwiki-pagelifecycle gwiki-pagetemplates gwiki-style-GracefulUndressed gwiki-style-mmLabs"

DESKTOP_PLUGINS="gwiki-admintools gwiki-s5slideshow gwiki-keywordsmarttags org.apache.pdfbox org.apache.poi gwiki-msotextextractor gwiki-pdftextextractor gwiki-rte-myspell gwiki-feed gwiki-blog"
GWIKIPUB_PLUGINS=$DESKTOP_PLUGINS

# gwiki-style-ProjectForge 
DEPLOYCMD="install"
INSTALLCMD="install"
COPYPLUGINS=true
COMPILE=true
STARTSTANDALONE=false

if [ $COMPILE == 'true' ]
then

for pm in $PRC
do 
	cd $pm;
	echo "Build $pm"
	$MVN $DEPLOYCMD;
	cd ..;
done;

cd plugins
for pm in $PLUGINS
do
	cd $pm;
	echo "Build $pm"
	$MVN $INSTALLCMD;
	cd ..;
done;
cd ..;
fi;

function copyPlugins()
{
 plugins=$1
 targetdir=$2
for pm in $plugins
do
	PVER=`cat plugins/$pm/pom.xml | perl -e 'while(<>) { if (/.*<version>(.*)<\/version><\!\-\- PLUGINVERSION*/) { print $1; } }'`;
	if [ "$PVER" ]
	then
		echo "copy plugins/$pm/target/$pm-$PVER.zip $targetdir";
		cp "plugins/$pm/target/$pm-$PVER.zip" "$targetdir"
	else
		echo "NO VERSION for $pm";
	fi;
done;
}
if [ $COPYPLUGINS == 'true' ]
then
	copyPlugins "$DESKTOP_PLUGINS" "./gwiki-standalone/src/main/external_resources/distr/gwiki/admin/plugins/"
	copyPlugins "$GWIKIPUB_PLUGINS" "./gwiki/src/main/external_resources/gwiki_pub/admin/plugins/"
fi;

cd ./gwiki
echo "Build gwiki"
$MVN $DEPLOYCMD
cd ../gwiki-standalone
echo "Build standalone"
$MVN $DEPLOYCMD
cd ../gwiki-webapp
echo "Build webapp"
$MVN $DEPLOYCMD
cd ../gwiki-wicket
echo "Build gwiki-wicket"
$MVN $DEPLOYCMD
cd ..

if [ $STARTSTANDALONE == 'true' ]
then
cd ./temp

rm -rf gwt
mkdir gwt
cd gwt
cp ../../gwiki-standalone/target/assembly/gwiki-standalone-$GV-bin.zip .
unzip gwiki-standalone-$GV-bin.zip
cd gwiki-standalone-$GV
cmd gwikiweb.cmd
fi

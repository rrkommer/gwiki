@echo off
echo start browser with http://localhost:8081/index
echo Users are gwikisu, gwikieditor, gwikiview. All with password gwiki

set JAVAHOME="c:\Program Files\Java\jdk1.6.0_07"
%JAVAHOME%\bin\java -jar jettygwiki-0.3.2.jar de.micromata.genome.gwiki.jetty.GWikiJettyStarter
MVN=/cygdrive/c/programr/apache-maven-3.0.4/bin/mvn
nohup $MVN exec:java -Dexec.mainClass="de.micromata.genome.gwiki.jetty.GWikiJettyStarter" -Dgwiki.properties.file=gwiki-rogmp3.properties&
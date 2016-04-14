//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.Pair;

/**
 * Utility to patch version number in files.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PatchVersion
{
  String version;

  String expected = null;

  boolean testOnly = false;

  List<Pair<String, String>> files = new ArrayList<Pair<String, String>>();

  public PatchVersion(String version)
  {
    this.version = version;
    String parentPomPattern = "(.+<artifactId>gwiki-parent</artifactId>.+?<groupId>de.micromata.genome.gwiki</groupId>.+?<version>)(.*?)(</version>.+)";
    String currPomPattern = "(.+<groupId>de.micromata.genome.gwiki</groupId>.+?<artifactId>gwiki</artifactId>.+?<version>)(.*?)(</version>.+)";
    
    files.add(Pair.make("../gwiki-parent/pom.xml", "(.+?<name>gwiki</name>.+?<version>)(.+?)(</version>.+)"));
    files.add(Pair.make("pom.xml", parentPomPattern));
    files.add(Pair.make("../gwiki-genome-dependencies/pom.xml", parentPomPattern));
    files.add(Pair.make("../gwiki-standalone/pom.xml", parentPomPattern));
    files.add(Pair.make("../gwiki-webapp/pom.xml", parentPomPattern));
    files.add(Pair.make("../gwiki-wicket/pom.xml", parentPomPattern));
    files.add(Pair.make("../gwiki-wicket-examples/pom.xml", parentPomPattern));
    
    // plugins
    files.add(Pair.make("../plugins/gwiki-admintools/pom.xml", parentPomPattern));
    files.add(Pair.make("../plugins/gwiki-admintools/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-blog/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-confluenceimporter/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-feed/pom.xml", parentPomPattern));
    files.add(Pair.make("../plugins/gwiki-feed/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-forum/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-fssvn/pom.xml", parentPomPattern));
    files.add(Pair.make("../plugins/gwiki-keywordsmarttag/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-logviewer/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-msotextextractor/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-pagelifecycle/pom.xml", parentPomPattern));
    files.add(Pair.make("../plugins/gwiki-pagelifecycle/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-pagetemplates/pom.xml", parentPomPattern));
    files.add(Pair.make("../plugins/gwiki-pagetemplates/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-pdfextractor/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-rte-myspell/pom.xml", parentPomPattern));
    files.add(Pair.make("../plugins/gwiki-rte-myspell/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-s5slideshow/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-sampleplugin/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-scheduler/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-style-ProjectForge/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-style-ProjectForge-example/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/gwiki-vfolder/pom.xml", currPomPattern));
    files.add(Pair.make("../plugins/org.apache.httpcomponents/pom.xml", parentPomPattern));
    files.add(Pair.make("../plugins/org.apache.pdfbox/pom.xml", parentPomPattern));
    files.add(Pair.make("../plugins/org.apache.poi/pom.xml", parentPomPattern));
    
    // internals
    files.add(Pair.make("src/main/java/de/micromata/genome/gwiki/model/GWikiVersion.java", "(.*?VERSION = \")(.+?)(\".+)"));
    files.add(Pair.make("../gwiki-standalone/src/main/external_resources/gwikistandalone/gwikiweb.cmd",
        "(.+?java -jar gwiki-standalone-)(.+?)(\\.jar.+)"));
    files.add(Pair.make("../gwiki-standalone/src/main/external_resources/gwikistandalone/gwikiweb.sh",
        "(.+?java -jar gwiki-standalone-)(.+?)(\\.jar.+)"));

  }

  public String readFile(String fname)
  {
    try {
      FileInputStream fin = new FileInputStream(new File(fname));
      String s = IOUtils.toString(fin);
      IOUtils.closeQuietly(fin);
      return s;
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  public void writeFile(String fname, String content)
  {
    try {
      FileOutputStream fout = new FileOutputStream(new File(fname));
      IOUtils.write(content, fout, "UTF-8");
      IOUtils.closeQuietly(fout);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  public void patch(Pair<String, String> p)
  {
    String f = readFile(p.getFirst());

    Pattern pattern = Pattern.compile(p.getSecond(), Pattern.MULTILINE | Pattern.DOTALL);
    Matcher m = pattern.matcher(f);
    if (m.matches() == false) {
      System.out.println("Does not match: " + p.getFirst() + "; Pattern: " + p.getSecond());
      return;
    }
    String g1 = m.group(1);
    String g2 = m.group(2);
    String g3 = m.group(3);
    if (StringUtils.isNotEmpty(expected) == true) {
      if (expected.equals(g2) == false) {
        System.out.println("Do not patch " + p.getFirst() + ". Expect: " + expected + "; got: " + g2);
        return;
      }
    }
    String ret = g1 + version + g3;
    System.out.println("Patched " + p.getFirst() + " from " + g2 + " to " + version);
    if (testOnly == true) {
      return;
    }
    writeFile(p.getFirst(), ret);
  }

  public void patch()
  {
    for (Pair<String, String> p : files) {
      patch(p);
    }
  }

  public static void main(String[] args)
  {
    PatchVersion pv = new PatchVersion(null);

    for (int i = 0; i < args.length; ++i) {
      String s = args[i];
      if (s.equals("-expected") == true) {
        if (args.length <= i + 1) {
          System.out.println("Required expected string");
          System.exit(1);
        }
        ++i;
        pv.expected = args[i];
        continue;
      }
      if (s.equals("-test") == true) {
        pv.testOnly = true;
        continue;
      }
      if (StringUtils.isEmpty(pv.version) == false) {
        System.out.println("Version already set");
        System.exit(1);
      } else {
        pv.version = s;
      }
    }
    if (StringUtils.isEmpty(pv.version) == true) {
      System.out.println("Missing required version");
      System.exit(1);
    }

    pv.patch();

  }
}

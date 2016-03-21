////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki;

import javax.servlet.ServletConfig;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiCpContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.web.GWikiServlet;

public class GWikiLoader
{
  private static GWikiWeb wikiWeb;
  private static GWikiServlet wikiServlet;

  public static GWikiWeb getGWikiWeb()
  {
    if (wikiWeb != null) {
      return wikiWeb;
    }
    GWikiBootstrapConfigLoader loader = new GWikiCpContextBootstrapConfigLoader()
    {
      @Override
      protected ConfigurableApplicationContext createApplicationContext(ServletConfig config, String fileName)
      {
        return new ClassPathXmlApplicationContext(new String[] { "GWikiTestContext.xml" }, false, null);
      }
    };
    wikiServlet = new GWikiServlet();
    //    wikiServlet.setDAOContext(loader.loadConfig(config);
    GWikiDAOContext bootStrapConfig = loader.loadConfig(null);
    GWikiWeb wikiWeb = new GWikiWeb(bootStrapConfig);
    return wikiWeb;
  }

  public static GWikiStandaloneContext getStandaloneContext()
  {
    GWikiStandaloneContext ret = new GWikiStandaloneContext();
    ret.setWikiWeb(getGWikiWeb());
    return ret;
  }

}

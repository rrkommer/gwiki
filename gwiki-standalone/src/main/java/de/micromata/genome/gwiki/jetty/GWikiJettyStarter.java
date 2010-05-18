////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

package de.micromata.genome.gwiki.jetty;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * Starter main class with embedded Jetty.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiJettyStarter
{
  public void start()
  {
    try {
      String contextFile = System.getProperty("de.micromata.genome.gwiki.contextfile");
      GwikiFileContextBootstrapConfigLoader cfgLoader = new GwikiFileContextBootstrapConfigLoader();
      if (StringUtils.isNotBlank(contextFile) == true) {
        cfgLoader.setFileName(contextFile);
      } else {
        contextFile = ".";
      }
      File f = new File(new File(contextFile).getParent(), "/log4j.properties");
      if (f.exists() == true) {
        PropertyConfigurator.configureAndWatch(f.getName(), 60 * 1000);
      }
      GWikiDAOContext wikibootcfg = cfgLoader.loadConfig(null);
      JettyConfig jettyConfig = (JettyConfig) cfgLoader.getBeanFactory().getBean("JettyConfig");
      Server server = new Server(jettyConfig.getPort());

      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      // upload applet needs this. set limit to 100mb
      context.setMaxFormContentSize(1024 * 1024 * 100);
      context.setContextPath(jettyConfig.getContextPath());
      context.setResourceBase(jettyConfig.getContextRoot());

      GWikiServlet wikiServlet = new GWikiServlet();
      ServletHolder wikiServletHolder = new ServletHolder(wikiServlet);
      wikiServlet.setDAOContext(wikibootcfg);
      context.addServlet(wikiServletHolder, "/*");
      server.setHandler(context);
      server.start();
      server.join();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static void main(String[] args)
  {
    new GWikiJettyStarter().start();
  }
}

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

package de.micromata.genome.gwiki.launcher;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiCpContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.search.expr.SearchExpressionIndexerCallback;
import de.micromata.genome.gwiki.web.GWikiLogHtmlWindowServlet;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.mgc.application.jetty.JettyServer;
import de.micromata.mgc.application.webserver.config.JettyConfigModel;

/**
 * Jetty server for GWiki.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiJettyServer extends JettyServer
{
  private GWikiDAOContext wikibootcfg;
  private GWikiServlet wikiServlet;

  public GWikiJettyServer()
  {
    super();
  }

  @Override
  protected ServletContextHandler createContextHandler(JettyConfigModel config)
  {
    String contextFile = "res:/StandaloneGWikiContext.xml";
    if (LocalSettings.get().getBooleanValue("gwiki.useContextXml", false) == true) {
      contextFile = LocalSettings.get().get("gwiki.contextfile", contextFile);
    }
    GWikiBootstrapConfigLoader cfgLoader;
    if (StringUtils.startsWith(contextFile, "res:") == true) {
      String fileName = contextFile.substring("res:".length());
      GWikiCpContextBootstrapConfigLoader cploader = new GWikiCpContextBootstrapConfigLoader();
      cploader.setFileName(fileName);
      cfgLoader = cploader;

    } else {
      GwikiFileContextBootstrapConfigLoader fcfgLoader = new GwikiFileContextBootstrapConfigLoader();
      if (StringUtils.isNotBlank(contextFile) == true) {
        fcfgLoader.setFileName(contextFile);
      } else {
        contextFile = ".";
      }
      cfgLoader = fcfgLoader;
    }

    wikibootcfg = cfgLoader.loadConfig(null);
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    wikiServlet = new GWikiServlet();
    ServletHolder wikiServletHolder = new ServletHolder(wikiServlet);
    wikiServlet.setDAOContext(wikibootcfg);
    context.addServlet(wikiServletHolder, "/*");
    GWikiLogHtmlWindowServlet logHtmlServlet = new GWikiLogHtmlWindowServlet();
    //    logHtmlServlet.init();
    ServletHolder logHtmlServletHolder = new ServletHolder(logHtmlServlet);
    context.addServlet(logHtmlServletHolder, "/loghtml");
    return context;
  }

  public void buildIndex(GWikiServlet wikiServlet, String contextPath)
  {
    final GWikiWeb nwiki = new GWikiWeb(wikiServlet.getDAOContext());
    String servletPath = "";
    nwiki.setContextPath(contextPath);
    nwiki.setServletPath(servletPath);
    try {
      final GWikiStandaloneContext ctx = new GWikiStandaloneContext(nwiki, wikiServlet, contextPath,
          servletPath);
      GWikiContext.setCurrent(ctx);
      nwiki.loadWeb();
      nwiki.runInPluginContext(new CallableX<Void, RuntimeException>()
      {

        @Override
        public Void call() throws RuntimeException
        {
          SearchExpressionIndexerCallback scb = new SearchExpressionIndexerCallback();
          scb.rebuildIndex(ctx, nwiki.getElementInfos(), true);
          return null;
        }
      });
    } catch (Exception ex) {
      GWikiLog.error("Failed to build index: " + ex.getMessage(), ex);
    } finally {
      GWikiContext.setCurrent(null);
    }

  }
}

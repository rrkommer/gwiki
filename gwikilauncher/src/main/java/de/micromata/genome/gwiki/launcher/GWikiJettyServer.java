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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiCpContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.search.expr.SearchExpressionIndexerCallback;
import de.micromata.genome.gwiki.web.GWikiLogHtmlWindowServlet;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.logging.GLog;
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
  private String contextPath;

  public GWikiJettyServer()
  {
    super();
  }

  @Override
  protected ServletContextHandler createContextHandler(JettyConfigModel config)
  {
    contextPath = config.getContextpath();
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

    wikiServlet.setContextPath(config.getContextpath());
    wikiServlet.setServletPath("");
    wikiServletHolder.setInitParameter("servletPath", "");
    wikiServletHolder.setInitParameter("contextPath", config.getContextpath());

    context.addServlet(wikiServletHolder, "/*");

    GWikiLogHtmlWindowServlet logHtmlServlet = new GWikiLogHtmlWindowServlet();
    //    logHtmlServlet.init();
    ServletHolder logHtmlServletHolder = new ServletHolder(logHtmlServlet);
    context.addServlet(logHtmlServletHolder, "/loghtml");
    context.setContextPath(config.getContextpath());
    return context;
  }

  public void buildIndex()
  {

    ExecutorService executor = Executors.newFixedThreadPool(1);
    executor.execute(() -> {
      try {
        Thread.sleep(3000);
        wikibootcfg.getWikiSelector().initWiki(wikiServlet, wikibootcfg);
        int maxWait = 20;
        for (int i = 0; i < maxWait; ++i) {
          Thread.sleep(1000);
          GWikiWeb web = GWikiWeb.get();
          if (web != null) {
            buildIndexInThread();
            break;
          }
        }
      } catch (InterruptedException ex) {

      }

    });
  }

  public void buildIndexInThread()
  {
    try {
      GWikiWeb nwiki = GWikiWeb.get();
      GWikiStandaloneContext ctx = GWikiStandaloneContext.create();
      GWikiContext.setCurrent(ctx);

      if (nwiki.findElementInfo("admin/GlobalTextIndex") != null) {
        return;
      }
      GLog.note(GWikiLogCategory.Wiki, "Start indexing GWiki");
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
      GLog.note(GWikiLogCategory.Wiki, "Finished indexing GWiki");

    } catch (Exception ex) {
      GWikiLog.error("Failed to build index: " + ex.getMessage(), ex);
    } finally {
      GWikiContext.setCurrent(null);
    }

  }
}

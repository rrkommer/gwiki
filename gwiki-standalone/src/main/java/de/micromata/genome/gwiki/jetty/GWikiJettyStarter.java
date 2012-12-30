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
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.search.expr.SearchExpressionIndexerCallback;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Starter main class with embedded Jetty.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiJettyStarter
{
  public void buildIndex(JettyConfig jettyConfig, GWikiServlet wikiServlet)
  {
    final GWikiWeb nwiki = new GWikiWeb(wikiServlet.getDAOContext());
    String servletPath = "";
    nwiki.setContextPath(jettyConfig.getContextPath());
    nwiki.setServletPath(servletPath);
    try {
      final GWikiStandaloneContext ctx = new GWikiStandaloneContext(nwiki, wikiServlet, jettyConfig.getContextPath(), servletPath);
      GWikiContext.setCurrent(ctx);
      nwiki.loadWeb();
      nwiki.runInPluginContext(new CallableX<Void, RuntimeException>() {

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

  void configureLogging(Server server, ServletContextHandler context)
  {
    String reqPath = System.getProperty("gwiki.jetty.logs");
    if (StringUtils.isBlank(reqPath) == true) {
      return;
    }
    HandlerCollection handlers = new HandlerCollection();
    RequestLogHandler requestLogHandler = new RequestLogHandler();
    handlers.setHandlers(new Handler[] { context, new DefaultHandler(), requestLogHandler});
    server.setHandler(handlers);

    NCSARequestLog requestLog = new NCSARequestLog(reqPath + "/jetty-yyyy_mm_dd.request.log");
    requestLog.setExtended(true);
    requestLog.setRetainDays(90);
    requestLog.setAppend(true);
    requestLog.setExtended(true);
    requestLog.setLogTimeZone("GMT");
    requestLogHandler.setRequestLog(requestLog);
  }

  public void start()
  {
    try {
      GWikiInitialSetup initSetup = new GWikiInitialSetup();
      boolean firstStart = initSetup.readCheckBasicSettings();

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
      if (jettyConfig.getContextPath() == null || jettyConfig.getContextPath().equals("/") == true) {
        jettyConfig.setContextPath("");
      }
      context.setContextPath(jettyConfig.getContextPath());
      context.setResourceBase(jettyConfig.getContextRoot());
      context.getSessionHandler().getSessionManager().setMaxInactiveInterval(jettyConfig.getSessionTimeout());
      GWikiServlet wikiServlet = new GWikiServlet();
      ServletHolder wikiServletHolder = new ServletHolder(wikiServlet);
      wikiServlet.setDAOContext(wikibootcfg);
      context.addServlet(wikiServletHolder, jettyConfig.getServletPath() + "*");
      server.setHandler(context);
      if (jettyConfig.getConnectors() != null) {
        for (Connector con : jettyConfig.getConnectors()) {
          server.addConnector(con);
        }
      }
      configureLogging(server, context);
      // Handler[] handlers = server.getHandlers();
      server.start();

      if (firstStart == true) {
        wikiServlet.setContextPath(jettyConfig.getContextPath());
        if (jettyConfig.getServletPath().equals("/") == true) {
          wikiServlet.setServletPath("");
        } else {
          wikiServlet.setServletPath(jettyConfig.getServletPath());
        }
        wikibootcfg.getWikiSelector().initWiki(wikiServlet, wikibootcfg);
        System.out.println("First time starting GWiki.\nBuild index. This can take a few minutes...");
        buildIndex(jettyConfig, wikiServlet);
        System.out.println("Finished intial indexing.");
      }

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

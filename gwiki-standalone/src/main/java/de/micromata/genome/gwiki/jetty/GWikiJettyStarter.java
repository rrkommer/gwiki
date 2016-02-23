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

package de.micromata.genome.gwiki.jetty;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.micromata.genome.gwiki.jetty.JettyStartListener.StartSucces;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiCpContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.search.expr.SearchExpressionIndexerCallback;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.LocalSettingsEnv;
import de.micromata.genome.util.runtime.Log4JInitializer;

/**
 * Starter main class with embedded Jetty.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiJettyStarter
{
  static {
    LocalSettings.localSettingsPrefixName = "gwiki";
  }

  private Server server;
  private JettyConfig jettyConfig;

  private GWikiDAOContext wikibootcfg;
  private GWikiServlet wikiServlet;

  public void buildIndex(JettyConfig jettyConfig, GWikiServlet wikiServlet)
  {
    final GWikiWeb nwiki = new GWikiWeb(wikiServlet.getDAOContext());
    String servletPath = "";
    nwiki.setContextPath(jettyConfig.getContextPath());
    nwiki.setServletPath(servletPath);
    try {
      final GWikiStandaloneContext ctx = new GWikiStandaloneContext(nwiki, wikiServlet, jettyConfig.getContextPath(),
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

  void configureLogging(Server server, ServletContextHandler context)
  {
    Log4JInitializer.copyLogConfigFileFromCp();
    Log4JInitializer.initializeLog4J();
    String reqPath = LocalSettings.get().get("gwiki.jetty.logs", "./log");
    if (StringUtils.isBlank(reqPath) == true) {
      return;
    }
    File target = new File(reqPath);
    if (target.exists() == false) {
      target.mkdirs();
    }
    HandlerCollection handlers = new HandlerCollection();
    RequestLogHandler requestLogHandler = new RequestLogHandler();
    handlers.setHandlers(new Handler[] { context, new DefaultHandler(), requestLogHandler });
    server.setHandler(handlers);

    NCSARequestLog requestLog = new NCSARequestLog(reqPath + "/gwiki-yyyy_mm_dd.request.log");
    requestLog.setExtended(true);
    requestLog.setRetainDays(90);
    requestLog.setAppend(true);
    requestLog.setExtended(true);
    requestLog.setLogTimeZone("GMT");
    requestLogHandler.setRequestLog(requestLog);
  }

  public void startCmdLine()
  {
    GWikiInitialSetup initSetup = new GWikiInitialSetup();
    boolean firstStart = initSetup.readCheckBasicSettings();
    start(null);
    if (firstStart == true) {
      onFirstStart();
    }
  }

  public void start()
  {
    start(new JettyStartListener()
    {
    });
  }

  public void start(JettyStartListener listener)
  {
    try {
      LocalSettings localSettings = LocalSettings.get();
      Log4JInitializer.initializeLog4J();
      localSettings.logloadedFiles();

      LocalSettingsEnv localSettingsEnv = LocalSettingsEnv.get();
      String contextFile = localSettings.get("gwiki.contextfile", "res:/StandaloneGWikiContext.xml");
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
      jettyConfig = (JettyConfig) cfgLoader.getBeanFactory().getBean("JettyConfig");
      server = new Server(jettyConfig.getPort());

      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      context.getInitParams().putAll(localSettings.getMap());

      // upload applet needs this. set limit to 100mb
      context.setMaxFormContentSize(1024 * 1024 * 100);
      if (jettyConfig.getContextPath() == null || jettyConfig.getContextPath().equals("/") == true) {
        jettyConfig.setContextPath("");
      }
      context.setContextPath(jettyConfig.getContextPath());
      context.setResourceBase(jettyConfig.getContextRoot());
      context.getSessionHandler().getSessionManager().setMaxInactiveInterval(jettyConfig.getSessionTimeout());
      wikiServlet = new GWikiServlet();
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

      System.out.println("You can now use gwiki with your web browser: " + LocalSettings.get().get("gwiki.public.url"));
      listener.started(StartSucces.Success, null);
      server.join();
    } catch (RuntimeException ex) {
      listener.started(StartSucces.Error, ex);
      throw ex;
    } catch (Exception ex) {
      listener.started(StartSucces.Error, ex);
      throw new RuntimeException(ex);
    }
  }

  public void onFirstStart()
  {

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

  public Server getServer()
  {
    return server;
  }

  public static void main(String[] args)
  {
    new GWikiJettyStarter().start();
  }
}

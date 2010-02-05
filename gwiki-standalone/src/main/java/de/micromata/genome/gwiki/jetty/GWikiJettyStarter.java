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
      context.setContextPath("/");
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

  // public void startViaWar()
  // {
  // // String jetty_home = System.getProperty("jetty.home", "..");
  //
  // Server server = new Server(8081);
  //
  // WebAppContext webapp = new WebAppContext();
  // webapp.setContextPath("/");
  // // webapp.setWar(war);
  // webapp.setWar(pluginhome);
  // server.setHandler(webapp);
  //
  // try {
  // server.start();
  // server.join();
  // } catch (RuntimeException ex) {
  // throw ex;
  // } catch (Exception ex) {
  // throw new RuntimeException(ex);
  // }
  //
  // }

  public static void main(String[] args)
  {
    new GWikiJettyStarter().start();
    // new GWikiJettyStarter().startViaWar();
  }
}

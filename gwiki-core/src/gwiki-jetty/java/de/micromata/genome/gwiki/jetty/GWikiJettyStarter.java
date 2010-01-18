package de.micromata.genome.gwiki.jetty;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.gwiki.web.StaticFileServlet;

public class GWikiJettyStarter
{
  public void start()
  {
    try {
      String contextFile = System.getProperty("de.micromata.genome.gwiki.contextfile");
      GwikiFileContextBootstrapConfigLoader cfgLoader = new GwikiFileContextBootstrapConfigLoader();
      if (StringUtils.isNotBlank(contextFile) == true) {
        cfgLoader.setFileName(contextFile);
      }
      GWikiDAOContext wikibootcfg = cfgLoader.loadConfig(null);
      JettyConfig jettyConfig = (JettyConfig) cfgLoader.getBeanFactory().getBean("JettyConfig");
      Server server = new Server(jettyConfig.getPort());

      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      context.setContextPath("/");
      context.setResourceBase(jettyConfig.getContextRoot());

      ServletHolder staticFileServlet = new ServletHolder(new StaticFileServlet());
      context.addServlet(staticFileServlet, "/static/*");
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

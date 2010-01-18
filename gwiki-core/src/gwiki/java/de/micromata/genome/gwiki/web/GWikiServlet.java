/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.10.2009
// Copyright Micromata 18.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.ClassUtils;

public class GWikiServlet extends HttpServlet
{

  private static final long serialVersionUID = 5250118043302579360L;

  private GWikiDAOContext daoContext;

  public GWikiWeb wiki;

  public static GWikiServlet INSTANCE;

  public GWikiServlet()
  {
    INSTANCE = this;
  }

  @Override
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    if (daoContext == null) {
      String className = config.getInitParameter("de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader.className");
      if (StringUtils.isBlank(className) == true) {
        throw new ServletException(
            "de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader.className is not set in ServletConfig for GWikiServlet");
      }
      GWikiBootstrapConfigLoader loader = ClassUtils.createDefaultInstance(className, GWikiBootstrapConfigLoader.class);
      daoContext = loader.loadConfig(config);
    }

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    doPost(req, resp);
  }

  protected void initWiki(HttpServletRequest req, HttpServletResponse resp)
  {
    if (wiki != null && wiki.getWikiConfig() != null) {
      return;
    }
    synchronized (this) {
      GWikiWeb nwiki = new GWikiWeb(daoContext);
      try {
        GWikiContext ctx = new GWikiContext(nwiki, this, req, resp);
        GWikiContext.setCurrent(ctx);
        nwiki.loadWeb();
      } finally {
        GWikiContext.setCurrent(null);
      }
      wiki = nwiki;
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    initWiki(req, resp);
    // String pi = req.getPathInfo();
    // String servletPath = req.getServletPath();
    long start = System.currentTimeMillis();
    GWikiContext ctx = new GWikiContext(wiki, this, req, resp);
    try {
      GWikiContext.setCurrent(ctx);
      wiki.serveWiki(ctx);
    } catch (Exception ex) {
      GWikiLog.error("GWikiWeb serve error: " + ex.getMessage(), ex);
    } finally {
      wiki.getLogging().addPerformance("GWikiServlet.doPost", System.currentTimeMillis() - start, 0);
      GWikiContext.setCurrent(null);
    }
  }

  public GWikiDAOContext getDAOContext()
  {
    return daoContext;
  }

  public void setDAOContext(GWikiDAOContext daoContext)
  {
    this.daoContext = daoContext;
  }

}

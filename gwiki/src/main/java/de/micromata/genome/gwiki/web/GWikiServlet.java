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
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.bradmcevoy.http.CompressingResponseHandler;
import com.bradmcevoy.http.DefaultResponseHandler;
import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.ResponseHandler;
import com.bradmcevoy.http.ServletRequest;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.gwiki.web.dav.FsDavResourceFactory;
import de.micromata.genome.gwiki.web.dav.office.FsDavOfficeResourceFactory;
import de.micromata.genome.util.types.TimeInMillis;
import de.micromata.genome.util.web.MimeUtils;

/**
 * Servlet for wiki, static and dav access.
 * 
 * @author roger
 * 
 */
public class GWikiServlet extends HttpServlet
{

  private static final long serialVersionUID = 5250118043302579360L;

  private GWikiDAOContext daoContext;

  public GWikiWeb wiki;

  /**
   * WebDAV
   */
  private HttpManager httpManager;

  public static GWikiServlet INSTANCE;

  public GWikiServlet()
  {
    INSTANCE = this;
  }

  @Override
  public void init(final ServletConfig config) throws ServletException
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

  protected void initWiki(final HttpServletRequest req, final HttpServletResponse resp)
  {
    if (wiki != null && wiki.getWikiConfig() != null) {
      return;
    }
    synchronized (this) {
      GWikiWeb nwiki = new GWikiWeb(daoContext);
      try {
        GWikiContext ctx = new GWikiContext(nwiki, this, req, resp);
        String servPath = req.getServletPath();
        GWikiContext.setCurrent(ctx);
        nwiki.setServletPath(servPath);
        nwiki.loadWeb();
      } finally {
        GWikiContext.setCurrent(null);
      }
      wiki = nwiki;
    }
  }

  protected String getWikiPage(final GWikiContext ctx)
  {
    String servPath = ctx.getRequest().getServletPath();
    String pathInfo = ctx.getRequest().getPathInfo();
    String page = servPath;
    if (StringUtils.isNotEmpty(pathInfo) == true) {
      page = pathInfo;
    }
    if (page.startsWith("/") == true) {
      page = page.substring(1);
    }
    return page;
  }

  @Override
  protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
  {
    initWiki(req, resp);
    long start = System.currentTimeMillis();
    GWikiContext ctx = new GWikiContext(wiki, this, req, resp);
    try {
      GWikiContext.setCurrent(ctx);
      String page = getWikiPage(ctx);

      if (page.equals("")) {
        page = "index";
      }

      if (page.equals("dav") == true || page.startsWith("dav/") == true) {
        serveWebDav(ctx);
        return;
      }
      final String method = req.getMethod();
      if (StringUtils.equals(method, "GET") == false && StringUtils.equals(method, "POST") == false) {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Gwiki Method " + method + " not supported");
        return;
      }
      if (page.startsWith("static/") == true) {
        serveStatic(page, ctx);
        return;
      }
      wiki.serveWiki(page, ctx);
    } catch (Exception ex) {
      GWikiLog.error("GWikiWeb serve error: " + ex.getMessage(), ex);
    } finally {
      wiki.getLogging().addPerformance("GWikiServlet.doPost", System.currentTimeMillis() - start, 0);
      GWikiContext.setCurrent(null);
    }
  }

  protected InputStream getStaticResource(final String res, final GWikiContext wikiContext) throws ServletException, IOException
  {
    if (daoContext.isStaticContentFromClassPath() == true) {
      return GWikiServlet.class.getResourceAsStream(res);
    } else {
      return getServletContext().getResourceAsStream(res);
    }
  }

  protected void serveStatic(final String page, final GWikiContext wikiContext) throws ServletException, IOException
  {
    String res = "/" + page;
    HttpServletResponse resp = wikiContext.getResponse();
    InputStream is = getStaticResource(res, wikiContext);
    if (is == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    long nt = new Date().getTime() + TimeInMillis.DAY;
    String mime = MimeUtils.getMimeTypeFromFile(res);
    if (StringUtils.equals(mime, "application/x-shockwave-flash")) {
      resp.setHeader("Cache-Control", "cache, must-revalidate");
      resp.setHeader("Pragma", "public");
    }
    resp.setDateHeader("Expires", nt);
    resp.setHeader("Cache-Control", "max-age=86400, public");
    if (mime != null) {
      resp.setContentType(mime);
    }

    byte[] data = IOUtils.toByteArray(is);
    IOUtils.closeQuietly(is);
    resp.setContentLength(data.length);
    IOUtils.write(data, resp.getOutputStream());
  }

  public synchronized HttpManager getHttpManager(final HttpServletRequest req)
  {
    if (httpManager != null) {
      return httpManager;
    }

    GWikiStorage wkStorage = GWikiServlet.INSTANCE.getDAOContext().getStorage();
    FileSystem storage = null;
    if (wkStorage instanceof GWikiFileStorage) {
      storage = ((GWikiFileStorage) wkStorage).getStorage();
    } else {
      throw new RuntimeException("GWiki not found or has no compatible storage");
    }
    String cpath = req.getContextPath();
    String servPath = req.getServletPath();
    // String pi = req.getPathInfo();
    String prefix = cpath + servPath + "/dav/";
    ResponseHandler responseHandler = new CompressingResponseHandler(new DefaultResponseHandler());
    FsDavResourceFactory fsfac = new FsDavResourceFactory(storage, prefix);
    fsfac.setInternalUserName(daoContext.getWebDavUserName());
    fsfac.setInternalPass(daoContext.getWebDavPasswordHash());
    boolean wordHtmlEdit = false;
    fsfac.setWordHtmlEdit(wordHtmlEdit);
    if (wordHtmlEdit == true) {
      httpManager = new HttpManager(new FsDavOfficeResourceFactory(wiki, fsfac), responseHandler);
    } else {
      httpManager = new HttpManager(fsfac, responseHandler);
    }
    return httpManager;
  }

  protected void serveWebDav(final GWikiContext ctx) throws ServletException, IOException
  {
    if (daoContext.isEnableWebDav() == false) {
      ctx.getResponse().sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "GWiki webdav not enabled");
      return;
    }
    Request request = new ServletRequest(ctx.getRequest());
    LogServletResponse response = new LogServletResponse(ctx.getResponse());
    getHttpManager(ctx.getRequest()).process(request, response);
  }

  public GWikiDAOContext getDAOContext()
  {
    return daoContext;
  }

  public void setDAOContext(final GWikiDAOContext daoContext)
  {
    this.daoContext = daoContext;
  }

}

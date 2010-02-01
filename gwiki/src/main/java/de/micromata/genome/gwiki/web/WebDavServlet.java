/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.web;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.bradmcevoy.http.CompressingResponseHandler;
import com.bradmcevoy.http.DefaultResponseHandler;
import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.ResponseHandler;
import com.bradmcevoy.http.ServletRequest;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.gwiki.web.dav.FsDavResourceFactory;
import de.micromata.genome.gwiki.web.dav.office.FsDavOfficeResourceFactory;

public class WebDavServlet extends HttpServlet
{

  private static final long serialVersionUID = -4638285765444826145L;

  private HttpManager httpManager;

  private boolean wordHtmlEdit = false;

  private String internalUserName;

  private String internalPass;

  public synchronized HttpManager getHttpManager(HttpServletRequest req)
  {
    if (httpManager != null)
      return httpManager;

    GWikiStorage wkStorage = GWikiServlet.INSTANCE.getDAOContext().getStorage();
    FileSystem storage = null;
    if (wkStorage instanceof GWikiFileStorage) {
      storage = ((GWikiFileStorage) wkStorage).getStorage();
    } else {
      throw new RuntimeException("GWiki not found or has no compatible storage");
    }
    String cpath = req.getContextPath();
    String servPath = req.getServletPath();
    //String pi = req.getPathInfo();
    String prefix = cpath + servPath;
    ResponseHandler responseHandler = new CompressingResponseHandler(new DefaultResponseHandler());
    FsDavResourceFactory fsfac = new FsDavResourceFactory(storage, prefix);
    fsfac.setInternalUserName(internalUserName);
    fsfac.setInternalPass(internalPass);
    fsfac.setWordHtmlEdit(wordHtmlEdit);
    if (wordHtmlEdit == true) {
      httpManager = new HttpManager(new FsDavOfficeResourceFactory(GWikiServlet.INSTANCE.wiki, fsfac), responseHandler);
    } else {
      httpManager = new HttpManager(fsfac, responseHandler);
    }
    return httpManager;
  }

  private void logRequest(HttpServletRequest req)
  {
    if (true)
      return;
    StringBuilder sb = new StringBuilder();
    sb.append(req.getMethod()).append(" ").append(req.getRequestURI()).append("\n");
    for (Enumeration en = req.getHeaderNames(); en.hasMoreElements();) {
      String name = (String) en.nextElement();
      String val = req.getHeader(name);
      sb.append(name).append(": ").append(val).append("\n");
    }
    sb.append("\n");
    GWikiContext ctx = GWikiContext.getCurrent();
    // ctx.getWikiWeb().getLogging().note("WebDav; " + req.getMethod() + " " + req.getRequestURI(), ctx);
    System.out.print(sb.toString());
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    logRequest(req);
    
    GWikiContext ctx = new GWikiContext(GWikiServlet.INSTANCE.wiki, GWikiServlet.INSTANCE, req, resp);

    try {
      GWikiContext.setCurrent(ctx);
      
      Request request = new ServletRequest(req);
      LogServletResponse response = new LogServletResponse(resp);
      getHttpManager(req).process(request, response);
      // System.out.println("Send:\n" + response.sb.toString());
    } finally {
      GWikiContext.setCurrent(null);
    }

  }

  @Override
  public void init(ServletConfig config) throws ServletException
  {
    if (StringUtils.equals("true", config.getInitParameter("wordHtmlEdit")) == true) {
      wordHtmlEdit = true;
    }
    internalUserName = config.getInitParameter("internalUserName");
    internalPass = config.getInitParameter("internalPass");
    super.init(config);
  }

  public String getInternalUserName()
  {
    return internalUserName;
  }

  public void setInternalUserName(String internalUserName)
  {
    this.internalUserName = internalUserName;
  }

  public String getInternalPass()
  {
    return internalPass;
  }

  public void setInternalPass(String internalPass)
  {
    this.internalPass = internalPass;
  }
}

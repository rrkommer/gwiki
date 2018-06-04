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

package de.micromata.genome.gwiki.web.dav;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.bradmcevoy.http.CompressingResponseHandler;
import com.bradmcevoy.http.DefaultResponseHandler;
import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.ResponseHandler;
import com.bradmcevoy.http.ServletRequest;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.gwiki.web.dav.office.FsDavOfficeResourceFactory;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * @deprecated the static content will also be provided by the GWikiServlet.
 * 
 */

@Deprecated
public class WebDavServlet extends HttpServlet
{

  private static final long serialVersionUID = -4638285765444826145L;
  private GWikiDavServer davServer;
  private HttpManager httpManager;

  private boolean wordHtmlEdit = false;

  private String internalUserName;

  private String internalPass;

  public synchronized HttpManager getHttpManager(HttpServletRequest req)
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
    String prefix = cpath + servPath;
    ResponseHandler responseHandler = new CompressingResponseHandler(new DefaultResponseHandler());
    FsDavResourceFactory fsfac = new FsDavResourceFactory(storage, prefix);
    fsfac.setInternalUserName(internalUserName);
    fsfac.setInternalPass(internalPass);
    fsfac.setWordHtmlEdit(wordHtmlEdit);
    if (wordHtmlEdit == true) {
      httpManager = new HttpManager(new FsDavOfficeResourceFactory(GWikiServlet.INSTANCE.getWikiWeb(), fsfac),
          responseHandler);
    } else {
      httpManager = new HttpManager(fsfac, responseHandler);
    }
    return httpManager;
  }

  private void logRequest(HttpServletRequest req)
  {
    if (true) {
      return;
      // StringBuilder sb = new StringBuilder();
      // sb.append(req.getMethod()).append(" ").append(req.getRequestURI()).append("\n");
      // for (Enumeration en = req.getHeaderNames(); en.hasMoreElements();) {
      // String name = (String) en.nextElement();
      // String val = req.getHeader(name);
      // sb.append(name).append(": ").append(val).append("\n");
      // }
      // sb.append("\n");
      // GWikiContext ctx = GWikiContext.getCurrent();
      // // ctx.getWikiWeb().getLogging().note("WebDav; " + req.getMethod() + " " + req.getRequestURI(), ctx);
      // System.out.print(sb.toString());
    }
  }

  protected void serveWebDav(GWikiContext ctx) throws ServletException, IOException
  {
    GWikiDAOContext daoContext = ctx.getWikiWeb().getDaoContext();
    if (daoContext.isEnableWebDav() == false) {
      ctx.getResponse().sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "GWiki webdav not enabled");
      return;
    }
    if (davServer == null) {
      davServer = new GWikiDavServer();
    }
    if (davServer != null) {
      davServer.serve(ctx.getWikiWeb(), daoContext, ctx);
    }
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    logRequest(req);

    GWikiContext ctx = new GWikiContext(GWikiServlet.INSTANCE.getWikiWeb(), GWikiServlet.INSTANCE, req, resp);

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

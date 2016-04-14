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

package de.micromata.genome.gwiki.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.bradmcevoy.http.CompressingResponseHandler;
import com.bradmcevoy.http.DefaultResponseHandler;
import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.ResponseHandler;
import com.bradmcevoy.http.ServletRequest;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.gwiki.web.dav.FsDavResourceFactory;
import de.micromata.genome.gwiki.web.dav.office.FsDavOfficeResourceFactory;

/**
 * @author roger
 * 
 */
public class GWikiDavServer
{
  /**
   * WebDAV
   */
  private HttpManager httpManager;

  public synchronized HttpManager getHttpManager(GWikiWeb wikiWeb, GWikiDAOContext daoContext, HttpServletRequest req)
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
    // String pi = req.getPathInfo();
    String prefix = cpath + servPath + "/dav/";
    ResponseHandler responseHandler = new CompressingResponseHandler(new DefaultResponseHandler());
    FsDavResourceFactory fsfac = new FsDavResourceFactory(storage, prefix);
    fsfac.setInternalUserName(daoContext.getWebDavUserName());
    fsfac.setInternalPass(daoContext.getWebDavPasswordHash());
    boolean wordHtmlEdit = false;
    fsfac.setWordHtmlEdit(wordHtmlEdit);
    if (wordHtmlEdit == true) {
      httpManager = new HttpManager(new FsDavOfficeResourceFactory(wikiWeb, fsfac), responseHandler);
    } else {
      httpManager = new HttpManager(fsfac, responseHandler);
    }
    return httpManager;
  }

  public void serve(GWikiWeb wikiWeb, GWikiDAOContext daoContext, GWikiContext ctx) throws ServletException, IOException
  {
    Request request = new ServletRequest(ctx.getRequest());
    LogServletResponse response = new LogServletResponse(ctx.getResponse());
    getHttpManager(wikiWeb, daoContext, ctx.getRequest()).process(request, response);
  }
}

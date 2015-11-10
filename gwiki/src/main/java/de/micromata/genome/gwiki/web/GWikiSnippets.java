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
package de.micromata.genome.gwiki.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Class to use GWiki Snippets in a servlet or jsp.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSnippets
{
  private GWikiWeb wikiWeb;

  private PageContext pageContext;

  private HttpServletRequest request;

  private HttpServletResponse response;

  private String backUrl;

  public GWikiSnippets(GWikiWeb wikiWeb, String backUrl, HttpServletRequest request, HttpServletResponse response)
  {
    this.backUrl = backUrl;
    this.wikiWeb = wikiWeb;
    if (this.wikiWeb == null) {
      this.wikiWeb = GWikiWeb.get();
    }
    if (GWikiServlet.INSTANCE == null) {
      throw new RuntimeException("Cannot initialize GWikiSnippets because no GWikiServlet.INSTANCE can be found");
    }
    if (this.wikiWeb == null) {
      GWikiServlet.INSTANCE.initWiki(request, response);
      this.wikiWeb = GWikiWeb.get();
    }
    if (this.wikiWeb == null) {
      throw new RuntimeException("Cannot initialize GWikiSnippets because no GWikiWeb can be found");
    }
    this.request = request;
    this.response = response;
    if (backUrl == null && request != null) {
      // String contextPath = request.getContextPath();
      String reqUri = request.getRequestURI();
      this.backUrl = reqUri;
      // if (StringUtils.length(contextPath) > 1) {
      // this.backUrl = reqUri.substring(contextPath.length());
      // }
      this.backUrl = "/" + this.backUrl; // double // that gwiki doesn't interpret as page id.
    }
    if (pageContext != null) {
      request = new HttpServletRequestWrapper(request) {

        @Override
        public String getContextPath()
        {
          return pageContext.getServletContext().getContextPath();
        }
      };
    }
  }

  public GWikiSnippets(PageContext pageContext, String backUrl)
  {
    this(null, pageContext, backUrl);
  }

  public GWikiSnippets(PageContext pageContext)
  {
    this(null, pageContext, null);
  }

  public GWikiSnippets(GWikiWeb wikiWeb, PageContext pageContext, String backUrl)
  {
    this(wikiWeb, backUrl, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
    this.pageContext = pageContext;
  }

  public void include(String pageId)
  {

    if (request != null && backUrl != null) {
      request.setAttribute("parentwel", backUrl);
    }
    // GWikiStandaloneContext wikiContext = new GWikiStandaloneContext(wikiWeb, );
    GWikiContext wikiContext = new GWikiContext(wikiWeb, GWikiServlet.INSTANCE, request, response);
    if (pageContext != null) {
      wikiContext.setPageContext(pageContext);
    }

    wikiWeb.serveWiki(wikiContext, pageId);
  }

  public GWikiWeb getWikiWeb()
  {
    return wikiWeb;
  }

  public void setWikiWeb(GWikiWeb wikiWeb)
  {
    this.wikiWeb = wikiWeb;
  }

  public HttpServletRequest getRequest()
  {
    return request;
  }

  public void setRequest(HttpServletRequest request)
  {
    this.request = request;
  }

  public HttpServletResponse getResponse()
  {
    return response;
  }

  public void setResponse(HttpServletResponse response)
  {
    this.response = response;
  }

  public String getBackUrl()
  {
    return backUrl;
  }

  public void setBackUrl(String backUrl)
  {
    this.backUrl = backUrl;
  }
}
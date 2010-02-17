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
package de.micromata.genome.gwiki.web;

import javax.servlet.http.HttpServletRequest;
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
    if (this.wikiWeb == null) {
      throw new RuntimeException("Cannot initialize GWikiSnippets because no GWikiWeb can be found");
    }
    if (GWikiServlet.INSTANCE == null) {
      throw new RuntimeException("Cannot initialize GWikiSnippets because no GWikiServlet.INSTANCE can be found");
    }
    this.request = request;
    this.response = response;

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
  }

  public void include(String pageId)
  {
    if (request != null && backUrl != null) {
      request.setAttribute("parentwel", backUrl);
    }
    // GWikiStandaloneContext wikiContext = new GWikiStandaloneContext(wikiWeb, );
    GWikiContext wikiContext = new GWikiContext(wikiWeb, GWikiServlet.INSTANCE, request, response);
    wikiWeb.serveWiki(wikiContext, pageId);
    // return wikiContext.getOutString();
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
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
package de.micromata.genome.gwiki.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiStandardWikiSelector implements GWikiWikiSelector
{
  protected GWikiWeb wiki;

  protected void initServletContext(GWikiServlet servlet, GWikiWeb nwiki, GWikiContext ctx)
  {
    if (servlet.getServletPath() == null) {
      servlet.setServletPath(ctx.getRealServletPath());
    }
    if (servlet.getContextPath() == null) {
      servlet.setContextPath(ctx.getRealContextPath());
    }
    nwiki.setContextPath(servlet.getContextPath());
    GWikiContext.setCurrent(ctx);
    nwiki.setServletPath(servlet.getServletPath());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiWikiSelector#getWikiWeb(de.micromata.genome.gwiki.web.GWikiServlet,
   * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void initWiki(GWikiServlet servlet, HttpServletRequest req, HttpServletResponse resp)
  {

    if (wiki != null && wiki.getWikiConfig() != null) {
      return;
    }
    synchronized (this) {
      GWikiWeb nwiki = new GWikiWeb(servlet.getDAOContext());
      try {
        GWikiContext ctx = new GWikiContext(nwiki, servlet, req, resp);
        initServletContext(servlet, nwiki, ctx);
        // set root wiki before load. because load may call GWikiWiki.get().
        if (wiki == null) {
          wiki = nwiki;
        }
        nwiki.loadWeb();
      } finally {
        GWikiContext.setCurrent(null);
      }
      wiki = nwiki;
    }
  }

  public void initWiki(GWikiServlet servlet, GWikiDAOContext daoContext)
  {
    if (wiki != null && wiki.getWikiConfig() != null) {
      return;
    }
    if (servlet.getServletPath() == null || servlet.getContextPath() == null) {
      throw new RuntimeException("servletPath and contextPath has to be set in GWikiServlet web.xml declaration");
    }
    GWikiWeb nwiki = new GWikiWeb(daoContext);
    GWikiStandaloneContext wikiContext = new GWikiStandaloneContext(nwiki, servlet, servlet.getContextPath(), servlet.getServletPath());
    try {
      GWikiContext.setCurrent(wikiContext);
      nwiki.setContextPath(servlet.getContextPath());
      nwiki.setServletPath(servlet.getServletPath());
      nwiki.loadWeb();
      wiki = nwiki;
    } finally {
      GWikiContext.setCurrent(null);
    }
  }

  public void deinitWiki(GWikiServlet servlet, HttpServletRequest req, HttpServletResponse resp)
  {
    // nothing
  }

  public GWikiWeb getWikiWeb(GWikiServlet servlet)
  {
    return getRootWikiWeb(servlet);
  }

  public GWikiWeb getTenantWikiWeb(GWikiServlet servlet, String tenant)
  {
    return null;
  }

  public GWikiWeb getRootWikiWeb(GWikiServlet servlet)
  {
    initWiki(servlet, servlet.getDAOContext());
    return wiki;
  }

  public boolean hasWikiWeb(GWikiServlet servlet)
  {
    return wiki != null;
  }

  public void enterTenant(GWikiContext wikiContext, String tenantId)
  {
    throw new RuntimeException("Does not support tenants");
  }

  public void clearTenant(GWikiContext wikiContext, String tenantId)
  {
    throw new RuntimeException("Does not support tenants");
  }

  public void createTenant(GWikiContext wikiContext, String tenantId)
  {
    throw new RuntimeException("Does not support tenants");
  }

  public void leaveTenant(GWikiContext wikiContext)
  {
    throw new RuntimeException("Does not support tenants");
  }
}

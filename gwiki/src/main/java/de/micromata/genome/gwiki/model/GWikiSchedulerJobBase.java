/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.12.2009
// Copyright Micromata 14.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * Base implementation of a GWikiSchedulerJob.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiSchedulerJobBase implements GWikiSchedulerJob
{

  private static final long serialVersionUID = 6520848817921436878L;

  protected GWikiContext wikiContext;

  protected Map<String, String> args;

  public abstract void call();

  public Object call(Map<String, String> args)
  {
    String servletPath = args.get("servletPath");
    String contextPath = args.get("contextPath");
    GWikiWeb wikiWeb = GWikiWeb.get();
    wikiContext = new GWikiStandaloneContext(wikiWeb, GWikiServlet.INSTANCE, contextPath, servletPath);
    this.args = args;
    try {
      GWikiContext.setCurrent(wikiContext);
      call();
      return null;
    } finally {
      GWikiContext.setCurrent(null);
    }

  }

  public String getPageId()
  {
    return args.get("pageId");
  }

  public GWikiContext getWikiContext()
  {
    return wikiContext;
  }

  public void setWikiContext(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

  public Map<String, String> getArgs()
  {
    return args;
  }

  public void setArgs(Map<String, String> args)
  {
    this.args = args;
  }

}

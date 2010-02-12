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

/**
 * Base implementation for GWikiSchedulerProvider.
 * 
 * @author roger
 * 
 */
public abstract class GWikiSchedulerProviderBase implements GWikiSchedulerProvider
{
  protected void prepareContext(GWikiContext wikiContext, Map<String, String> args)
  {
    args.put("contextPath", wikiContext.getRequest().getContextPath());
    args.put("servletPath", wikiContext.getRequest().getServletPath());
  }

}

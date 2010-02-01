/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   17.11.2009
// Copyright Micromata 17.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.filter;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * common base for filter events.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiFilterEvent
{
  protected GWikiContext wikiContext;

  public GWikiFilterEvent(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

  public GWikiContext getWikiContext()
  {
    return wikiContext;
  }

  public void setWikiContext(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

}

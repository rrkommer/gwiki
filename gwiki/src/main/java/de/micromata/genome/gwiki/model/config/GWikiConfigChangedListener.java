/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.01.2010
// Copyright Micromata 09.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.config;

import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilter;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilterEvent;

/**
 * Filter for changing global GWiki configuration.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiConfigChangedListener implements GWikiPageChangedFilter
{
  public Void filter(GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter> chain, GWikiPageChangedFilterEvent event)
  {
    // should on first position, otherwise filter recursion.
    chain.nextFilter(event);
    if (event.getNewInfo() == null) {
      return null;
    }
    if (event.getNewInfo().getId().equals("admin/config/GWikiConfig") == true) {
      event.getWikiWeb().reloadWikiConfig();
    }
    return null;
  }
}

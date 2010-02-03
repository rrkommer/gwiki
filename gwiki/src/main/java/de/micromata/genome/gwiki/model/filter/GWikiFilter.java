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


/**
 * Filter interface for filtering internal events.
 * 
 * @param R Return value of the filter
 * @param E Event type of the filter
 * @param F filter itself.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiFilter<R, E extends GWikiFilterEvent, F extends GWikiFilter<R, E, F>>
{
  R filter(GWikiFilterChain<R, E, F> chain, E event);
}

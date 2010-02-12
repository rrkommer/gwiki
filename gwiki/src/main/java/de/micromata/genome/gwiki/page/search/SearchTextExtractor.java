/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * create a raw text extract.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface SearchTextExtractor
{
  String getRawText(GWikiContext ctx, SearchQuery query, SearchResult sr);

  int getWeight();
}

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search.expr;

import java.util.Collection;
import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

/**
 * A search expression interface.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface SearchExpression
{
  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query);

  /**
   * after filter, remember which words was called.
   * 
   * @return
   */
  public List<String> getLookupWords();
}

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import java.util.Collection;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Trivial grep'er
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface ContentSearcher
{
  public void rebuildIndex(GWikiContext wikiContext, String pageId);

  /**
   * return a collection of searchmacros, like parentpageid:id, etc.
   * 
   * @return
   */
  Collection<String> getSearchMacros();

  /**
   * 
   * @param searchExpression
   * @return pageId's
   */
  QueryResult search(GWikiContext ctx, SearchQuery query);

}

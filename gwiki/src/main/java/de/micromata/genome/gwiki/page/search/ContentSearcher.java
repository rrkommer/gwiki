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

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Trivial grep'er
 * 
 * @author roger@micromata.de
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

  // List<SearchResult> search(GWikiContext context, Matcher<String> searchExpression, int maxCount);
  /**
   * Will be called if element will be added.
   */
  void addElement(GWikiElement el);

  void removeElement(GWikiElementInfo ei);

  void replaceElement(GWikiElement el);

}

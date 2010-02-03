/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   10.11.2009
// Copyright Micromata 10.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search.expr;

import de.micromata.genome.gwiki.page.search.SearchResult;

/**
 * Compares values in the ElementInfo properties
 * 
 * @author roger@micromata.de
 * 
 */
@Deprecated
public class SearchResultComparatorProps extends SearchResultComparatorBase
{
  private String key;

  public SearchResultComparatorProps(String key)
  {
    this.key = key;
  }

  @Override
  public int compareThis(SearchResult o1, SearchResult o2)
  {
    String v1 = o1.getElementInfo().getProps().getStringValue(key);
    String v2 = o2.getElementInfo().getProps().getStringValue(key);
    if (v1 == v2)
      return 0;
    if (v1 == null)
      return -1;
    if (v2 == null)
      return 1;
    return v1.compareTo(v2);
  }

  public String toString()
  {
    return "prop:" + key + (desc == true ? " desc" : " asc");
  }
}

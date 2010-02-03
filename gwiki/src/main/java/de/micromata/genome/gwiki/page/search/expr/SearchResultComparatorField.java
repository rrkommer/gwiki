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
public class SearchResultComparatorField extends SearchResultComparatorBase
{
  private SearchExpressionFieldSelektor fieldSelector;

  public SearchResultComparatorField(SearchExpressionFieldSelektor fieldSelector)
  {
    this.fieldSelector = fieldSelector;
  }

  @Override
  public int compareThis(SearchResult o1, SearchResult o2)
  {
    String v1 = fieldSelector.getField(o1);
    String v2 = fieldSelector.getField(o2);
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
    return fieldSelector.toString() + (desc == true ? " desc" : " asc");
  }
}

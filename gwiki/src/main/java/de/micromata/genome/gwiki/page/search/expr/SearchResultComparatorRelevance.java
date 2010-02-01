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

public class SearchResultComparatorRelevance extends SearchResultComparatorBase
{

  @Override
  public int compareThis(SearchResult o1, SearchResult o2)
  {
    if (o1.getRelevance() == o2.getRelevance())
      return 0;
    if (o1.getRelevance() < o2.getRelevance())
      return -1;
    return 1;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("relevance").append(desc == true ? " desc" : " asc");
    if (nextComparator != null) {
      sb.append(", ");
      sb.append(nextComparator.toString());
    }
    return sb.toString();
  }
}

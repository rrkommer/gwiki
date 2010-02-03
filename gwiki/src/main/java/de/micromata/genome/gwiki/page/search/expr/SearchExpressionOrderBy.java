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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionOrderBy extends SearchExpressionUnary
{
  public List<SearchResultComparator> comparators = new ArrayList<SearchResultComparator>();

  public SearchExpressionOrderBy()
  {
    super();
  }

  public SearchExpressionOrderBy(SearchExpression nested)
  {
    super(nested);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    Collection<SearchResult> res;
    if (nested != null) {
      res = nested.filter(ctx, query);
    } else {
      res = query.getResults();
    }
    List<SearchResult> sortList;
    if (res instanceof List) {
      sortList = (List<SearchResult>) res;
    } else {
      sortList = new ArrayList<SearchResult>();
      sortList.addAll(res);
    }
    for (SearchResultComparator cmp : comparators) {
      Collections.sort(sortList, cmp);
    }
    return sortList;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if (nested != null) {
      sb.append(nested.toString());
    }
    sb.append(" order by ");
    boolean isFirst = true;
    for (SearchResultComparator c : comparators) {
      if (isFirst == true) {
        isFirst = false;
      } else {
        sb.append(", ");
      }
      sb.append(c.toString());
    }
    return sb.toString();

  }

  public List<String> getLookupWords()
  {
    if (nested == null) {
      return Collections.emptyList();
    }
    return nested.getLookupWords();
  }

  public void addComparator(SearchResultComparator comp)
  {
    comparators.add(comp);
  }

  public List<SearchResultComparator> getComparators()
  {
    return comparators;
  }

  public void setComparators(List<SearchResultComparator> comparators)
  {
    this.comparators = comparators;
  }

}

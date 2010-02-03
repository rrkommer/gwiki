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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionNotIn extends SearchExpressionUnary
{

  public SearchExpressionNotIn()
  {
  }

  public SearchExpressionNotIn(SearchExpression nested)
  {
    super(nested);

  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    Set<String> allKeys = new HashSet<String>();

    Collection<SearchResult> nrs = nested.filter(ctx, query);
    for (SearchResult sr : nrs) {
      allKeys.add(sr.getPageId());
    }
    Collection<SearchResult> result = new ArrayList<SearchResult>();
    for (SearchResult sr : query.getResults()) {
      if (allKeys.contains(sr.getPageId()) == false) {
        result.add(sr);
      }
    }
    return result;
  }

  public List<String> getLookupWords()
  {
    return Collections.emptyList();
  }

  @Override
  public String toString()
  {
    return "not(" + nested.toString() + ")";
  }

}

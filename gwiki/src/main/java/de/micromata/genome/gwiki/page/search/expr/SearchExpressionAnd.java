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
import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionAnd extends SearchExpressionBinary
{

  public SearchExpressionAnd()
  {

  }

  public SearchExpressionAnd(SearchExpression left, SearchExpression right)
  {
    super(left, right);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    List<SearchExpression> list = new ArrayList<SearchExpression>();
    list.add(left);
    list.add(right);
    // Map<String, SearchResult> ret = new TreeMap<String, SearchResult>();

    Collection<SearchResult> src = left.filter(ctx, query);
    SearchQuery tq = new SearchQuery(query);
    tq.setResults(src);
    Collection<SearchResult> les = right.filter(ctx, tq);
    return les;
  }

  @Override
  public String toString()
  {
    return "and(" + left.toString() + ", " + right.toString() + ")";
  }

  

}

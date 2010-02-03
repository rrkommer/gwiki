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
import java.util.Collections;
import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionExact extends SearchExpressionUnary
{

  public SearchExpressionExact()
  {
  }

  public SearchExpressionExact(SearchExpression nested)
  {
    super(nested);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    throw new RuntimeException("SearchExpressionExact currently not supported");
  }

  public List<String> getLookupWords()
  {
    return Collections.emptyList();
  }

}

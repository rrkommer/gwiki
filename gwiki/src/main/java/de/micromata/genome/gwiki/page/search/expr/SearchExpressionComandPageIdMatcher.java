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
import de.micromata.genome.gwiki.page.search.SearchTextExtractorBase;

/**
 * SearchExpression look at the pageId.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SearchExpressionComandPageIdMatcher extends SearchExpressionCommand
{
  public SearchExpressionComandPageIdMatcher(String command, SearchExpression nested)
  {
    super(command, nested);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    // List<SearchResult> ret = new ArrayList<SearchResult>();
    SearchQuery nq = new SearchQuery(query);
    nq.setTextExtractor(new SearchTextExtractorBase(1) {

      public String getRawText(GWikiContext ctx, SearchQuery query, SearchResult sr)
      {
        return sr.getPageId();
      }
    });
    Collection<SearchResult> fsr = nested.filter(ctx, nq);
    return fsr;
  }

  public List<String> getLookupWords()
  {
    return Collections.emptyList();
  }

}

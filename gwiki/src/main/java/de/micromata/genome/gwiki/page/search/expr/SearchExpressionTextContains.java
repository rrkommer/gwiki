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

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.SearchTextExtractor;

public class SearchExpressionTextContains extends SearchExpressionText implements SearchExpressionFieldSelektor
{

  public SearchExpressionTextContains(String text)
  {
    super(text);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    List<SearchResult> ret = new ArrayList<SearchResult>();
    SearchQuery sq = new SearchQuery(query);
    sq.setSearchExpression(text);
    if (query.getTextExtractor() == null) {
      for (SearchResult sr : query.getResults()) {
        SearchResult res = SearchUtils.findResult(ctx, sq, sr);
        if (res != null) {
          ret.add(res);
        }
      }
    } else {
      SearchTextExtractor tex = query.getTextExtractor();
      for (SearchResult sr : query.getResults()) {
        String comp = tex.getRawText(ctx, query, sr);
        if (comp == null) {
          continue;
        }
        if (StringUtils.containsIgnoreCase(comp, text) == true) {
          ret.add(sr);
        }
      }
    }
    return ret;
  }

  @Override
  public String toString()
  {
    return "contains(" + text + ")";
  }

  public String getField(SearchResult sr)
  {
    return text;
  }
}

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
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionComandWikiSpace extends SearchExpressionCommand
{
  public SearchExpressionComandWikiSpace(String command, SearchExpression nested)
  {
    super(command, nested);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    String text = ((SearchExpressionText) nested).getText();
    List<SearchResult> ret = new ArrayList<SearchResult>();
    for (SearchResult s : query.getResults()) {
      if (StringUtils.equals(s.getElementInfo().getWikiSpace(ctx), text) == true) {
        ret.add(s);
      }
    }
    return ret;
  }

  public List<String> getLookupWords()
  {
    return Collections.emptyList();
  }

}

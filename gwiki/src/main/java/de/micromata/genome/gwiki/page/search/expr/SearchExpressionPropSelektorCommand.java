/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.11.2009
// Copyright Micromata 14.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search.expr;

import java.util.Collection;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionPropSelektorCommand extends SearchExpressionCommand implements SearchExpressionFieldSelektor
{
  private String key;

  public SearchExpressionPropSelektorCommand(String command, SearchExpression nested)
  {
    super(command, nested);
    this.key = nested.getLookupWords().get(0);
  }

  public String getField(SearchResult sr)
  {
    if (GWikiPropKeys.PAGEID.equals(key) == true) {
      return sr.getElementInfo().getId();
    }
    return sr.getElementInfo().getProps().getStringValue(key);
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public List<String> getLookupWords()
  {
    // TODO Auto-generated method stub
    return null;
  }

}

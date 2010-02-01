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
import java.util.List;

public abstract class SearchExpressionList implements SearchExpression
{

  protected List<SearchExpression> list = new ArrayList<SearchExpression>();

  public SearchExpressionList()
  {

  }

  public SearchExpressionList(List<SearchExpression> list)
  {
    this.list = list;
  }

  public List<String> getLookupWords()
  {
    List<String> ret = new ArrayList<String>();
    for (SearchExpression sr : list) {
      ret.addAll(sr.getLookupWords());
    }
    return ret;
  }

  public List<SearchExpression> getList()
  {
    return list;
  }

  public void setList(List<SearchExpression> list)
  {
    this.list = list;
  }

}

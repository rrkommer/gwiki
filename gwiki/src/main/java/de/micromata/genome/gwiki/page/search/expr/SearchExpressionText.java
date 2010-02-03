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

public abstract class SearchExpressionText implements SearchExpression
{
  protected String text;

  public SearchExpressionText(String text)
  {
    this.text = text;
  }
  public List<String> getLookupWords()
  {
    List<String> ret = new ArrayList<String>();
    ret.add(text);
    return ret;
  }

  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  @Override
  public String toString()
  {
    return text;
  }

}

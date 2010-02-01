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

public abstract class SearchExpressionUnary implements SearchExpression
{
  protected SearchExpression nested;

  public SearchExpressionUnary()
  {

  }

  public SearchExpressionUnary(SearchExpression nested)
  {
    this.nested = nested;
  }

  public SearchExpression getNested()
  {
    return nested;
  }

  public void setNested(SearchExpression nested)
  {
    this.nested = nested;
  }

}

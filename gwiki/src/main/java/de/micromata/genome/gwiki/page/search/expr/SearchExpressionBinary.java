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

/**
 * Combine two SearchExpression .
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class SearchExpressionBinary implements SearchExpression
{

  protected SearchExpression left;

  protected SearchExpression right;

  public SearchExpressionBinary()
  {

  }

  public SearchExpressionBinary(SearchExpression left, SearchExpression right)
  {
    this.left = left;
    this.right = right;
  }

  public List<String> getLookupWords()
  {
    List<String> ret = new ArrayList<String>();
    ret.addAll(left.getLookupWords());
    ret.addAll(right.getLookupWords());
    return ret;
  }

  public SearchExpression getLeft()
  {
    return left;
  }

  public void setLeft(SearchExpression left)
  {
    this.left = left;
  }

  public SearchExpression getRight()
  {
    return right;
  }

  public void setRight(SearchExpression right)
  {
    this.right = right;
  }

}

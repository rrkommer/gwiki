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

public abstract class SearchExpressionCommand extends SearchExpressionUnary
{
  protected String command;

  public SearchExpressionCommand(String command, SearchExpression nested)
  {
    super(nested);
    this.command = command;
  }

  @Override
  public String toString()
  {
    return "command(" + command + ":" + nested.toString() + ")";
  }
}

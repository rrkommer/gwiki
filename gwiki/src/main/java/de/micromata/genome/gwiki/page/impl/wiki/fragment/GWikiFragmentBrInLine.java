/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   31.12.2009
// Copyright Micromata 31.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

public class GWikiFragmentBrInLine extends GWikiFragmentBr
{

  private static final long serialVersionUID = -8245596367479475761L;

  public GWikiFragmentBrInLine()
  {
    super();
  }

  @Override
  public String getHtml()
  {
    return "<br/>\n";
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append("\\\\\n");
  }

}

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

public class GWikiFragmentBr extends GWikiFragmentHtml
{

  private static final long serialVersionUID = -8245596367479475761L;

  public GWikiFragmentBr()
  {
    super("<br/>\n");
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append("\n");
  }

}

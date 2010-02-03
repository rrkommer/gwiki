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

import java.util.List;

public class GWikiFragmentFixedFont extends GWikiFragmentDecorator
{

  private static final long serialVersionUID = -8245596367479475761L;

  public GWikiFragmentFixedFont(List<GWikiFragment> childs)
  {
    super("<span style=\"font-family:monospace\">", "</span>", childs);
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append("{{");
    getChildSouce(sb);
    sb.append("}}");
  }

}

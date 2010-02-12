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


/**
 * list elements inside a list.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentLi extends GWikiFragmentDecorator
{

  private static final long serialVersionUID = 5564320556231411169L;

  GWikiFragmentList listfrag;

  public GWikiFragmentLi(GWikiFragmentList listfrag)
  {
    super("<li>", "</li>\n");
    this.listfrag = listfrag;
  }

  public GWikiFragmentLi(GWikiFragmentList listfrag, List<GWikiFragment> childs)
  {
    super("<li>", "</li>", childs);
    this.listfrag = listfrag;
  }

  public void getSource(StringBuilder sb)
  {
    sb.append(listfrag.getListTag()).append(" ");
    getChildSouce(sb);
    sb.append("\n");
  }
}

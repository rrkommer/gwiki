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
 * Text decorations like italic, bold.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentTextDeco extends GWikiFragmentDecorator
{

  private static final long serialVersionUID = -4065615892012017382L;

  private char wikiTag;

  public GWikiFragmentTextDeco(char wikiTag, String prefix, String suffix, List<GWikiFragment> childs)
  {
    super(prefix, suffix, childs);
    this.wikiTag = wikiTag;
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append(wikiTag);
    getChildSouce(sb);
    sb.append(wikiTag);
  }

}

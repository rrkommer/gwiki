/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   01.11.2009
// Copyright Micromata 01.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

public abstract class GWikiFragementBase implements GWikiFragment
{

  private static final long serialVersionUID = -1842371131960720605L;

  public abstract void getSource(StringBuilder sb);

  public String getSource()
  {
    StringBuilder sb = new StringBuilder();
    getSource(sb);
    return sb.toString();
  }

  public String toString()
  {
    return getSource();
  }

  public void iterate(GWikiFragmentVisitor visitor)
  {
    visitor.begin(this);
    visitor.end(this);
  }
}

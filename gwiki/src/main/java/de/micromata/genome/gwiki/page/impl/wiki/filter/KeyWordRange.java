/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.11.2009
// Copyright Micromata 18.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

public class KeyWordRange
{
  public int start;

  public int end;

  public List<GWikiElementInfo> links = new ArrayList<GWikiElementInfo>();

  public KeyWordRange()
  {

  }

  public KeyWordRange(int start, int end, List<GWikiElementInfo> links)
  {
    this.start = start;
    this.end = end;
    this.links = links;
  }
}
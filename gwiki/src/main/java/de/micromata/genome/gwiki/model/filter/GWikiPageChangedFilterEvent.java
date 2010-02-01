/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.01.2010
// Copyright Micromata 09.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.filter;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiPageChangedFilterEvent extends GWikiFilterEvent
{
  private GWikiWeb wikiWeb;

  private GWikiElementInfo newInfo;

  private GWikiElementInfo oldInfo;

  public GWikiPageChangedFilterEvent(GWikiContext wikiContext, GWikiWeb wikiWeb, GWikiElementInfo newInfo, GWikiElementInfo oldInfo)
  {
    super(wikiContext);
    this.wikiWeb = wikiWeb;
    this.newInfo = newInfo;
    this.oldInfo = oldInfo;

  }

  public GWikiElementInfo getNewInfo()
  {
    return newInfo;
  }

  public void setNewInfo(GWikiElementInfo newInfo)
  {
    this.newInfo = newInfo;
  }

  public GWikiElementInfo getOldInfo()
  {
    return oldInfo;
  }

  public void setOldInfo(GWikiElementInfo oldInfo)
  {
    this.oldInfo = oldInfo;
  }

  public GWikiWeb getWikiWeb()
  {
    return wikiWeb;
  }

  public void setWikiWeb(GWikiWeb wikiWeb)
  {
    this.wikiWeb = wikiWeb;
  }

}

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   17.11.2009
// Copyright Micromata 17.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.filter;

import java.util.Map;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Filter when page infos will be loaded.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiLoadElementInfosFilterEvent extends GWikiFilterEvent
{
  private Map<String, GWikiElementInfo> pageInfos;

  public GWikiLoadElementInfosFilterEvent(GWikiContext wikiContext, Map<String, GWikiElementInfo> pageInfos)
  {
    super(wikiContext);
    this.pageInfos = pageInfos;
  }

  public Map<String, GWikiElementInfo> getPageInfos()
  {
    return pageInfos;
  }

  public void setPageInfos(Map<String, GWikiElementInfo> pageInfos)
  {
    this.pageInfos = pageInfos;
  }

}

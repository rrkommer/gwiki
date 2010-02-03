/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.01.2010
// Copyright Micromata 09.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.filter;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilter;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilterEvent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;

public class GWikiKeywordPageChangedFilter implements GWikiPageChangedFilter
{
  public Void filter(GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter> chain, GWikiPageChangedFilterEvent event)
  {
    GWikiKeywordLoadElementInfosFilter chf = GWikiKeywordLoadElementInfosFilter.getInstance();
    if (chf == null) {
      return chain.nextFilter(event);
    }

    if (event.getOldInfo() != null && event.getNewInfo() != null) {
      String oKeyWords = event.getOldInfo().getProps().getStringValue(GWikiPropKeys.KEYWORDS);
      String nKeyWords = event.getNewInfo().getProps().getStringValue(GWikiPropKeys.KEYWORDS);
      if (StringUtils.equals(oKeyWords, nKeyWords) == true) {
        return chain.nextFilter(event);
      }

    } else if (event.getOldInfo() != null) {
      String oKeyWords = event.getOldInfo().getProps().getStringValue(GWikiPropKeys.KEYWORDS);
      if (StringUtils.isEmpty(oKeyWords) == false) {
      } else {
        return chain.nextFilter(event);
      }
    } else if (event.getNewInfo() != null) {
      String nKeyWords = event.getNewInfo().getProps().getStringValue(GWikiPropKeys.KEYWORDS);
      if (StringUtils.isEmpty(nKeyWords) == false) {
      } else {
        return chain.nextFilter(event);
      }
    }
    chf.clearKeywords();
    event.getWikiWeb().getDaoContext().getPageCache().clearCompiledFragments(GWikiWikiPageArtefakt.class);
    return chain.nextFilter(event);
  }
}

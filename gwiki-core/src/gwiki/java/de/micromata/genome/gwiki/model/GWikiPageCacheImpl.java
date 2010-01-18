/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.12.2009
// Copyright Micromata 18.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections15.map.ReferenceMap;

import de.micromata.genome.util.matcher.Matcher;

/**
 * Holds the ElementInfo and Elements (cache) for the Wiki.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiPageCacheImpl implements GWikiPageCache
{

  private Map<String, GWikiElement> cachedPages = Collections.synchronizedMap(new ReferenceMap<String, GWikiElement>(ReferenceMap.HARD,
      ReferenceMap.SOFT));

  private Map<String, GWikiElementInfo> pageInfoMap = Collections.emptyMap();

  private boolean logNode = false;

  public GWikiPageCacheImpl()
  {

  }

  // public GWikiPageCacheImpl(GWikiWeb wikiWeb)
  // {
  // this.wikiWeb = wikiWeb;
  // }

  public void clearCachedPages()
  {
    if (logNode == true) {
      GWikiLog.info("clearPages");
    }
    cachedPages.clear();
  }

  public void clearCachedPage(String pageId)
  {
    cachedPages.remove(pageId);
  }

  public GWikiElementInfo getPageInfo(String pageId)
  {
    return pageInfoMap.get(pageId);
  }

  public void putPageInfo(GWikiElementInfo ei)
  {
    if (logNode == true) {
      GWikiLog.info("put pageInfo: " + this + ": " + ei.getId());
    }
    pageInfoMap.put(ei.getId(), ei);
  }

  public boolean hasCachedPage(String pageId)
  {
    return cachedPages.containsKey(pageId);
  }

  public GWikiElement getPage(String pageId)
  {
    return cachedPages.get(pageId);
  }

  public Collection<GWikiElementInfo> getPageInfos()
  {
    if (logNode == true) {
      GWikiLog.info("getPageInfos: " + this);
    }
    return pageInfoMap.values();
  }

  public void removePageInfo(String pageId)
  {
    if (logNode == true) {
      GWikiLog.info("remove pageId: " + pageId);
    }
    pageInfoMap.remove(pageId);
  }

  public boolean hasPageInfo(String pageId)
  {
    return pageInfoMap.containsKey(pageId);
  }

  public void putCachedPage(String pageId, GWikiElement el)
  {
    if (logNode == true) {
      GWikiLog.info("put page: " + pageId);
    }
    cachedPages.put(pageId, el);
  }

  public Map<String, GWikiElement> getCachedPages()
  {
    return cachedPages;
  }

  public void setCachedPages(Map<String, GWikiElement> cachedPages)
  {
    this.cachedPages = cachedPages;
  }

  public Map<String, GWikiElementInfo> getPageInfoMap()
  {
    if (logNode == true) {
      GWikiLog.info("getPageInfoMap: " + this);
    }

    return pageInfoMap;
  }

  public void setPageInfoMap(Map<String, GWikiElementInfo> pageInfos)
  {
    this.pageInfoMap = pageInfos;
  }

  public void initPageCache(GWikiWeb wikiWeb)
  {
    // nothing
  }

  public void registerCacheEvent(Matcher<String> pageIdMatcher, String propName)
  {
    throw new UnsupportedOperationException("GWikiPageCacheImpl.registerCacheEvent not supported");
  }

  public void clearCompiledFragments(Class< ? extends GWikiArtefakt< ? extends Serializable>> toClear)
  {
    throw new UnsupportedOperationException("GWikiPageCacheImpl.clearCompiledFragments not supported");
  }
}

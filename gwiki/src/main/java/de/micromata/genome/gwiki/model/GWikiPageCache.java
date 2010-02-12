/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   29.12.2009
// Copyright Micromata 29.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Interface for caching pages.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiPageCache
{
  public void clearCachedPages();

  public void clearCachedPage(String pageId);

  public GWikiElementInfo getPageInfo(String pageId);

  public void putPageInfo(GWikiElementInfo ei);

  public boolean hasCachedPage(String pageId);

  public GWikiElement getPage(String pageId);

  public Collection<GWikiElementInfo> getPageInfos();

  public void removePageInfo(String pageId);

  public boolean hasPageInfo(String pageId);

  public void putCachedPage(String pageId, GWikiElement el);

  /**
   * Use this map for reading only, otherwise event handling will not work.
   * 
   * @return
   */
  public Map<String, GWikiElementInfo> getPageInfoMap();

  public void setPageInfoMap(Map<String, GWikiElementInfo> pageInfos);

  /**
   * Will be called once, after initializing web.
   * 
   * @param wikiWeb
   */
  public void initPageCache(GWikiWeb wikiWeb);

  /**
   * On the cached pages, set setCompiledObject to null.
   * 
   * @param toClear
   */
  public void clearCompiledFragments(Class< ? extends GWikiArtefakt< ? extends Serializable>> toClear);
}

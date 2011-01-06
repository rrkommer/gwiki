////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.model;

import java.io.Serializable;
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

  /**
   * 
   * @return number of element infos in this gwikiweb.
   */
  public int getElementInfoCount();

  public Iterable<GWikiElementInfo> getPageInfos();

  public void removePageInfo(String pageId);

  public boolean hasPageInfo(String pageId);

  public void putCachedPage(String pageId, GWikiElement el);

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

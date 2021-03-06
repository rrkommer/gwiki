//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;
import org.apache.commons.collections4.map.ReferenceMap;

import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Holds the ElementInfo and Elements (cache) for the Wiki.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@Deprecated
public class GWikiPageCacheImpl implements GWikiPageCache
{

  private Map<String, GWikiElement> cachedPages = Collections
      .synchronizedMap(new ReferenceMap<String, GWikiElement>(ReferenceStrength.HARD,
          ReferenceStrength.SOFT));

  private Map<String, GWikiElementInfo> pageInfoMap = Collections.emptyMap();

  private boolean logNode = false;

  public GWikiPageCacheImpl()
  {

  }

  // public GWikiPageCacheImpl(GWikiWeb wikiWeb)
  // {
  // this.wikiWeb = wikiWeb;
  // }

  @Override
  public void clearCachedPages()
  {
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "clearPages");
    }
    cachedPages.clear();
  }

  @Override
  public void clearCachedPage(String pageId)
  {
    cachedPages.remove(pageId);
  }

  @Override
  public GWikiElementInfo getPageInfo(String pageId)
  {
    return pageInfoMap.get(pageId);
  }

  @Override
  public void putPageInfo(GWikiElementInfo ei)
  {
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "put pageInfo: " + this + ": " + ei.getId());
    }
    pageInfoMap.put(ei.getId(), ei);
  }

  @Override
  public boolean hasCachedPage(String pageId)
  {
    return cachedPages.containsKey(pageId);
  }

  @Override
  public GWikiElement getPage(String pageId)
  {
    return cachedPages.get(pageId);
  }

  @Override
  public Collection<GWikiElementInfo> getPageInfos()
  {
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "getPageInfos: " + this);
    }
    return pageInfoMap.values();
  }

  @Override
  public void removePageInfo(String pageId)
  {
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "remove pageId: " + pageId);
    }
    pageInfoMap.remove(pageId);
  }

  @Override
  public boolean hasPageInfo(String pageId)
  {
    return pageInfoMap.containsKey(pageId);
  }

  @Override
  public void putCachedPage(String pageId, GWikiElement el)
  {
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "put page: " + pageId);
    }
    cachedPages.put(pageId, el);
  }

  @Override
  public int getElementInfoCount()
  {
    return pageInfoMap.size();
  }

  public Map<String, GWikiElement> getCachedPages()
  {
    return cachedPages;
  }

  public void setCachedPages(Map<String, GWikiElement> cachedPages)
  {
    this.cachedPages = cachedPages;
  }

  @Override
  public void setPageInfoMap(Map<String, GWikiElementInfo> pageInfos)
  {
    this.pageInfoMap = pageInfos;
  }

  @Override
  public void initPageCache(GWikiWeb wikiWeb)
  {
    // nothing
  }

  public void registerCacheEvent(Matcher<String> pageIdMatcher, String propName)
  {
    throw new UnsupportedOperationException("GWikiPageCacheImpl.registerCacheEvent not supported");
  }

  @Override
  public void clearCompiledFragments(Class<? extends GWikiArtefakt<? extends Serializable>> toClear)
  {
    throw new UnsupportedOperationException("GWikiPageCacheImpl.clearCompiledFragments not supported");
  }

  @Override
  public GWikiPageCacheInfo getPageCacheInfo()
  {
    return new GWikiPageCacheInfo();
  }
}

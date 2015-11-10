////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
package de.micromata.genome.gwiki.model.mpt;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPageCache;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWeb;

/**
 * Combined page cache
 * 
 * TODO deleted pages will not be recognised.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiCombinedPageCache implements GWikiPageCache
{
  /**
   * This is the primary cache, which may hold additanally/modified pages.
   */
  private GWikiPageCache primaryCache;

  /**
   * secondary/orignal/root cache.
   */
  private GWikiPageCache secondaryCache;

  public GWikiCombinedPageCache(GWikiPageCache primaryCache, GWikiPageCache secondaryCache)
  {
    this.primaryCache = primaryCache;
    this.secondaryCache = secondaryCache;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#clearCachedPage(java.lang.String)
   */
  public void clearCachedPage(String pageId)
  {
    primaryCache.clearCachedPage(pageId);
    secondaryCache.clearCachedPage(pageId);

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#clearCachedPages()
   */
  public void clearCachedPages()
  {
    primaryCache.clearCachedPages();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#clearCompiledFragments(java.lang.Class)
   */
  public void clearCompiledFragments(Class< ? extends GWikiArtefakt< ? extends Serializable>> toClear)
  {
    primaryCache.clearCompiledFragments(toClear);
    secondaryCache.clearCompiledFragments(toClear);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#getPage(java.lang.String)
   */
  public GWikiElement getPage(String pageId)
  {
    if (primaryCache.hasPageInfo(pageId) == true) {
      return primaryCache.getPage(pageId);
    }
    return secondaryCache.getPage(pageId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#getPageInfo(java.lang.String)
   */
  public GWikiElementInfo getPageInfo(String pageId)
  {

    GWikiElementInfo ei = primaryCache.getPageInfo(pageId);
    if (ei != null) {
      return ei;
    }
    return secondaryCache.getPageInfo(pageId);
  }

  public int getElementInfoCount()
  {
    return primaryCache.getElementInfoCount() + secondaryCache.getElementInfoCount();
  }

  class CombinedElementInfoIterator implements Iterator<GWikiElementInfo>
  {
    boolean inSecondary = false;

    Iterator<GWikiElementInfo> primary;

    Iterator<GWikiElementInfo> secondary;

    GWikiElementInfo secNext = null;

    public CombinedElementInfoIterator()
    {
      primary = primaryCache.getPageInfos().iterator();
      secondary = secondaryCache.getPageInfos().iterator();
    }

    private void seekNextSec()
    {
      if (secNext != null) {
        return;
      }
      if (secondary.hasNext() == false) {
        return;
      }
      secNext = secondary.next();
      if (primaryCache.hasCachedPage(secNext.getId()) == false) {
        return;
      }
      secNext = null;
      seekNextSec();
    }

    public boolean hasNext()
    {
      if (inSecondary == false) {
        if (primary.hasNext() == true) {
          return true;
        }
        inSecondary = true;
        return hasNext();
      }
      if (secondary.hasNext() == false) {
        return false;
      }
      seekNextSec();
      return secNext != null;
    }

    public GWikiElementInfo next()
    {

      if (inSecondary == false) {
        return primary.next();
      }
      seekNextSec();
      if (secNext == null) {
        throw new NoSuchElementException();
      }
      GWikiElementInfo ret = secNext;
      secNext = null;
      return ret;
    }

    public void remove()
    {
      throw new UnsupportedOperationException("Removing page info not supported");

    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#getPageInfos()
   */
  public Iterable<GWikiElementInfo> getPageInfos()
  {
    return new Iterable<GWikiElementInfo>() {

      public Iterator<GWikiElementInfo> iterator()
      {
        return new CombinedElementInfoIterator();
      }
    };
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#hasCachedPage(java.lang.String)
   */
  public boolean hasCachedPage(String pageId)
  {
    return primaryCache.hasCachedPage(pageId) == true || secondaryCache.hasCachedPage(pageId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#hasPageInfo(java.lang.String)
   */
  public boolean hasPageInfo(String pageId)
  {
    return primaryCache.hasPageInfo(pageId) == true || secondaryCache.hasPageInfo(pageId) == true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#initPageCache(de.micromata.genome.gwiki.model.GWikiWeb)
   */
  public void initPageCache(GWikiWeb wikiWeb)
  {
    primaryCache.initPageCache(wikiWeb);

  }

  protected GWikiPageCache getCacheToWrite(GWikiElementInfo ei)
  {
    if (primaryCache.hasPageInfo(ei.getId()) == true) {
      return primaryCache;
    }
    String tid = ei.getProps().getStringValue(GWikiPropKeys.TENANT_ID);
    if (StringUtils.isNotEmpty(tid) == true) {
      if (StringUtils.equals(GWikiWeb.get().getTenantId(), tid) == true) {
        return primaryCache;
      }
    }
    return secondaryCache;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#putCachedPage(java.lang.String, de.micromata.genome.gwiki.model.GWikiElement)
   */
  public void putCachedPage(String pageId, GWikiElement el)
  {
    getCacheToWrite(el.getElementInfo()).putCachedPage(pageId, el);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#putPageInfo(de.micromata.genome.gwiki.model.GWikiElementInfo)
   */
  public void putPageInfo(GWikiElementInfo ei)
  {
    getCacheToWrite(ei).putPageInfo(ei);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#removePageInfo(java.lang.String)
   */
  public void removePageInfo(String pageId)
  {
    primaryCache.removePageInfo(pageId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#setPageInfoMap(java.util.Map)
   */
  public void setPageInfoMap(Map<String, GWikiElementInfo> pageInfos)
  {
    primaryCache.setPageInfoMap(pageInfos);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#getPageCacheInfo()
   */
  public GWikiPageCacheInfo getPageCacheInfo()
  {
    GWikiPageCacheInfo prim = primaryCache.getPageCacheInfo();
    GWikiPageCacheInfo sec = secondaryCache.getPageCacheInfo();
    GWikiPageCacheInfo ret = new GWikiPageCacheInfo();
    ret.setElementInfosCount(prim.getElementInfosCount() + sec.getElementInfosCount());
    ret.setElementInfosSize(prim.getElementInfosSize() + sec.getElementInfosSize());
    ret.setPageCount(prim.getPageCount() + sec.getPageCount());
    ret.setPageSize(prim.getPageSize() + sec.getPageSize());
    return ret;
  }

}

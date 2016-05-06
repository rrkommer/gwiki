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
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * Interface for caching pages.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiPageCache
{
  /**
   * information about internal state of cache. -1 values means don't know.
   * 
   * @author Roger Rene Kommer (r.kommer@micromata.de)
   * 
   */
  public static class GWikiPageCacheInfo
  {
    private int elementInfosCount = -1;

    private long elementInfosSize = -1;

    private int pageCount = -1;

    private long pageSize = -1;

    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Element count: ").append(elementInfosCount)
          .append("; elementInfoSize: ").append(FileUtils.byteCountToDisplaySize(elementInfosSize))
          .append("; pageCount: ").append(pageCount)
          .append("; pageSize: ").append(FileUtils.byteCountToDisplaySize(pageSize));
      return sb.toString();
    }

    public int getElementInfosCount()
    {
      return elementInfosCount;
    }

    public void setElementInfosCount(int elementInfosCount)
    {
      this.elementInfosCount = elementInfosCount;
    }

    public long getElementInfosSize()
    {
      return elementInfosSize;
    }

    public void setElementInfosSize(long elementInfosSize)
    {
      this.elementInfosSize = elementInfosSize;
    }

    public int getPageCount()
    {
      return pageCount;
    }

    public void setPageCount(int pageCount)
    {
      this.pageCount = pageCount;
    }

    public long getPageSize()
    {
      return pageSize;
    }

    public void setPageSize(long pageSize)
    {
      this.pageSize = pageSize;
    }

  }

  public GWikiPageCacheInfo getPageCacheInfo();

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
  public void clearCompiledFragments(Class<? extends GWikiArtefakt<? extends Serializable>> toClear);
}

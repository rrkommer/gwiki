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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gdbfs.FileSystemEvent;
import de.micromata.genome.gdbfs.FileSystemEventListener;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiConfigElement;
import de.micromata.genome.gwiki.page.impl.GWikiFileAttachment;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.string.EndsWithMatcher;
import de.micromata.genome.util.types.Pair;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * Holds the ElementInfo and Elements (cache) for the Wiki.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageCacheTimedImpl implements GWikiPageCache
{

  /**
   * The standard life time.
   */
  private long standardLifeTime = TimeInMillis.MINUTE;

  /**
   * The cached pages.
   */
  private Map<String, Pair<Long, GWikiElement>> cachedPages = newCachePagesMap();

  /**
   * The page info map.
   */
  private Map<String, GWikiElementInfo> pageInfoMap = Collections.emptyMap();

  /**
   * The log node.
   */
  private boolean logNode = false;

  /**
   * The wiki web.
   */
  private GWikiWeb wikiWeb;

  /**
   * The no cache pages.
   */
  private Matcher<String> noCachePages;

  /**
   * The no cache page ids.
   */
  private Matcher<String> noCachePageIds;

  /**
   * Instantiates a new g wiki page cache timed impl.
   */
  public GWikiPageCacheTimedImpl()
  {
    noCachePageIds = new BooleanListRulesFactory<String>().createMatcher("tmp/*,arch/*,*/arch/*");
    noCachePages = noCachePageIds;
  }

  /**
   * New cache pages map.
   *
   * @return the map
   */
  protected Map<String, Pair<Long, GWikiElement>> newCachePagesMap()
  {
    return new HashMap<String, Pair<Long, GWikiElement>>();
  }

  /**
   * New cache pages map.
   *
   * @param oldMap the old map
   * @return the map
   */
  protected Map<String, Pair<Long, GWikiElement>> newCachePagesMap(Map<String, Pair<Long, GWikiElement>> oldMap)
  {
    Map<String, Pair<Long, GWikiElement>> nm = new HashMap<String, Pair<Long, GWikiElement>>(oldMap.size());
    nm.putAll(oldMap);
    return nm;
  }

  /**
   * New page info map.
   *
   * @param oldMap the old map
   * @return the map
   */
  protected Map<String, GWikiElementInfo> newPageInfoMap(Map<String, GWikiElementInfo> oldMap)
  {
    Map<String, GWikiElementInfo> nm = new HashMap<String, GWikiElementInfo>(oldMap.size());
    nm.putAll(oldMap);
    return nm;
  }

  @Override
  public GWikiElementInfo getPageInfo(String pageId)
  {
    return pageInfoMap.get(pageId);
  }

  @Override
  public void putPageInfo(GWikiElementInfo ei)
  {
    putPageInfo(ei, true);
  }

  /**
   * Put page info.
   *
   * @param ei the ei
   * @param notify the notify
   */
  public void putPageInfo(GWikiElementInfo ei, boolean notify)
  {
    if (noCachePageIds.match(ei.getId()) == true) {
      return;
    }
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "put pageInfo: " + this + ": " + ei.getId());
    }

    GWikiElementInfo oldEi = pageInfoMap.get(ei.getId());
    Map<String, GWikiElementInfo> nm = newPageInfoMap(pageInfoMap);
    nm.put(ei.getId(), ei);
    pageInfoMap = nm;
    if (notify == true) {
      wikiWeb.getFilter().pageChanged(GWikiContext.getCurrent(), wikiWeb, ei, oldEi);
    }
  }

  @Override
  public boolean hasCachedPage(String pageId)
  {
    return cachedPages.containsKey(pageId);
  }

  @Override
  public void clearCachedPages()
  {
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "clearPages");
    }
    cachedPages = newCachePagesMap();
    wikiWeb.initETag();
  }

  @Override
  public void clearCachedPage(String pageId)
  {
    if (cachedPages.containsKey(pageId) == false) {
      return;
    }
    Map<String, Pair<Long, GWikiElement>> nm = newCachePagesMap(cachedPages);
    nm.remove(pageId);
    cachedPages = nm;
  }

  @Override
  public void clearCompiledFragments(Class<? extends GWikiArtefakt<? extends Serializable>> toClear)
  {
    for (Pair<Long, GWikiElement> p : cachedPages.values()) {
      GWikiElement el = p.getSecond();
      Map<String, GWikiArtefakt<?>> map = new HashMap<String, GWikiArtefakt<?>>();
      el.collectParts(map);
      for (GWikiArtefakt<?> art : map.values()) {
        if (toClear.isAssignableFrom(art.getClass()) == false) {
          continue;
        }
        art.setCompiledObject(null);
      }
    }
  }

  /**
   * Gets the life time.
   *
   * @param element the element
   * @return the life time
   */
  private long getLifeTime(GWikiElement element)
  {
    final GWikiMetaTemplate metaTemplate = element.getElementInfo().getMetaTemplate();
    if (metaTemplate == null) {
      if (element instanceof GWikiConfigElement) {
        return -1;
      }
      return standardLifeTime;
    }
    if (element instanceof GWikiFileAttachment) {
      final GWikiFileAttachment att = (GWikiFileAttachment) element;
      int size = att.getSize();
      if (size < 4096) {
        return TimeInMillis.DAY;
      } else if (size > 1024 * 250) {
        return 0;
      }
    }
    return metaTemplate.getElementLifeTime();
  }

  @Override
  public GWikiElement getPage(String pageId)
  {
    Pair<Long, GWikiElement> p = cachedPages.get(pageId);
    if (p == null) {
      return null;
    }
    if (p.getFirst() == -1) {
      return p.getSecond();
    }
    long lifeTime = getLifeTime(p.getSecond());
    if (lifeTime == -1) {
      p.setFirst(lifeTime);
    } else {
      p.setFirst(System.currentTimeMillis() + lifeTime);
    }
    return p.getSecond();
  }

  @Override
  public Iterable<GWikiElementInfo> getPageInfos()
  {
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "getPageInfos: " + this);
    }
    return pageInfoMap.values();
  }

  @Override
  public int getElementInfoCount()
  {
    return pageInfoMap.size();
  }

  @Override
  public void removePageInfo(String pageId)
  {
    removePageInfo(pageId, true);
  }

  /**
   * Removes the page info.
   *
   * @param pageId the page id
   * @param notify the notify
   */
  public void removePageInfo(String pageId, boolean notify)
  {
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "remove pageId: " + pageId);
    }
    GWikiElementInfo oldInfo = pageInfoMap.get(pageId);
    if (oldInfo == null) {
      return;
    }
    Map<String, GWikiElementInfo> nm = newPageInfoMap(pageInfoMap);
    nm.remove(pageId);
    pageInfoMap = nm;
    if (notify == true) {
      wikiWeb.getFilter().pageChanged(GWikiContext.getCurrent(), wikiWeb, null, oldInfo);
    }
  }

  @Override
  public boolean hasPageInfo(String pageId)
  {
    return pageInfoMap.containsKey(pageId);
  }

  /**
   * Clear old items.
   *
   * @param nm the nm
   * @param now the now
   */
  protected void clearOldItems(Map<String, Pair<Long, GWikiElement>> nm, long now)
  {
    Collection<String> toRemove = null;
    for (Map.Entry<String, Pair<Long, GWikiElement>> me : nm.entrySet()) {
      final long lt = me.getValue().getFirst();
      if (lt != -1 && lt < now) {
        if (toRemove == null) {
          toRemove = new ArrayList<String>();
        }
        toRemove.add(me.getKey());
        if (logNode == true) {
          GLog.note(GWikiLogCategory.Wiki, "Remove element from cache: " + me.getKey());
        }
      }
    }
    if (toRemove == null) {
      return;
    }

    for (String rk : toRemove) {
      nm.remove(rk);
    }
  }

  @Override
  public void putCachedPage(String pageId, GWikiElement el)
  {
    if (noCachePages.match(pageId) == true) {
      return;
    }
    if (logNode == true) {
      GLog.note(GWikiLogCategory.Wiki, "put page: " + pageId);
    }
    long lifeTime = getLifeTime(el);
    if (lifeTime == 0) {
      return;
    }
    Map<String, Pair<Long, GWikiElement>> nm = newCachePagesMap(cachedPages);
    final long now = System.currentTimeMillis();
    if (lifeTime == -1) {
      nm.put(pageId, Pair.make(lifeTime, el));
    } else {
      nm.put(pageId, Pair.make(now + lifeTime, el));
    }
    clearOldItems(nm, now);
    cachedPages = nm;
  }

  @Override
  public void initPageCache(GWikiWeb wikiWeb)
  {
    this.wikiWeb = wikiWeb;
    initListener();
  }

  /**
   * Inits the listener.
   */
  protected void initListener()
  {
    final GWikiStorage storage = wikiWeb.getStorage();
    storage.getFileSystem().registerListener(null, new EndsWithMatcher<String>(GWikiStorage.SETTINGS_SUFFIX),
        new FileSystemEventListener()
        {

          @Override
          public void onFileSystemChanged(FileSystemEvent event)
          {
            String fileName = event.getFileName();
            String id = fileName.substring(0, fileName.length() - GWikiStorage.SETTINGS_SUFFIX.length());
            if (id.startsWith("/") == true) {
              id = id.substring(1);
            }
            switch (event.getEventType()) {
              case Created:
              case Modified: {
                if (noCachePageIds.match(id) == true) {
                  break;
                }
                GWikiElementInfo oldEi = pageInfoMap.get(id);

                if (oldEi != null && oldEi.getLoadedTimeStamp() >= event.getTimeStamp()) {
                  break;
                }
                GWikiElementInfo newEi = storage.loadElementInfo(id);
                if (newEi != null) {
                  putPageInfo(newEi, false);
                }
                if (newEi != null && newEi.getLoadedTimeStamp() >= event.getTimeStamp()) {
                  wikiWeb.getFilter().pageChanged(GWikiContext.getCurrent(), wikiWeb, newEi, oldEi);
                }
                clearCachedPage(id);
                break;
              }
              case Deleted: {
                GWikiElementInfo oldEi = pageInfoMap.get(id);
                if (oldEi != null) {
                  wikiWeb.getFilter().pageChanged(GWikiContext.getCurrent(), wikiWeb, null, oldEi);
                }
                clearCachedPage(id);
                removePageInfo(id, false);
                break;
              }
              case Renamed: {
                GWikiElementInfo newEi = null;
                if (noCachePageIds.match(id) == false) {
                  newEi = storage.loadElementInfo(id);
                  if (newEi != null) {
                    putPageInfo(newEi, false);
                  }
                }
                String oldFileName = event.getOldFileName();
                if (oldFileName.endsWith(GWikiStorage.SETTINGS_SUFFIX) == false) {
                  break;
                }
                String oldId = oldFileName.substring(0, oldFileName.length() - GWikiStorage.SETTINGS_SUFFIX.length());
                if (oldId.startsWith("/") == true) {
                  oldId = oldId.substring(1);
                }
                GWikiElementInfo oldEi = pageInfoMap.get(id);
                if (oldEi != null) {
                  wikiWeb.getFilter().pageChanged(GWikiContext.getCurrent(), wikiWeb, null, oldEi);
                }
                if (newEi != null) {
                  wikiWeb.getFilter().pageChanged(GWikiContext.getCurrent(), wikiWeb, newEi, null);
                }
                clearCachedPage(oldId);
                removePageInfo(oldId, false);
                break;
              }
            }
          }
        });
  }

  public Map<String, GWikiElementInfo> getPageInfoMap()
  {
    if (logNode == true) {
      GLog.info(GWikiLogCategory.Wiki, "getPageInfoMap: " + this);
    }

    return pageInfoMap;
  }

  @Override
  public void setPageInfoMap(Map<String, GWikiElementInfo> pageInfos)
  {
    this.pageInfoMap = pageInfos;
  }

  public long getStandardLifeTime()
  {
    return standardLifeTime;
  }

  public void setStandardLifeTime(long standardLifeTime)
  {
    this.standardLifeTime = standardLifeTime;
  }

  public Matcher<String> getNoCachePages()
  {
    return noCachePages;
  }

  public void setNoCachePages(Matcher<String> noCachePages)
  {
    this.noCachePages = noCachePages;
  }

  public Matcher<String> getNoCachePageIds()
  {
    return noCachePageIds;
  }

  public void setNoCachePageIds(Matcher<String> noCachePageIds)
  {
    this.noCachePageIds = noCachePageIds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPageCache#getPageCacheInfo()
   */
  @Override
  public GWikiPageCacheInfo getPageCacheInfo()
  {
    GWikiPageCacheInfo ret = new GWikiPageCacheInfo();
    ret.setElementInfosCount(pageInfoMap.size());
    ret.setPageCount(cachedPages.size());
    Matcher<String> m = new BooleanListRulesFactory<String>()
        .createMatcher(
            "+*,-de.micromata.genome.gwiki.model.GWikiWeb,-de.micromata.genome.gwiki.model.config.GWikiMetaTemplate");
    ret.setElementInfosSize(PrivateBeanUtils.getBeanSize(pageInfoMap, m));
    ret.setPageSize(PrivateBeanUtils.getBeanSize(cachedPages));
    return ret;
  }

}

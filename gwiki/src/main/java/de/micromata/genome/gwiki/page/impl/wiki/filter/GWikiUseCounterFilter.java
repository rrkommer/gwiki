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

package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.PropUtils;
import de.micromata.genome.util.types.TimeInMillis;

public class GWikiUseCounterFilter implements GWikiServeElementFilter
{

  public static final String PageViewCounterfile = "admin/_PageViewCounter.properties";

  private static GWikiProps lastReadUseCounterMap = new GWikiProps();

  private static Map<String, Integer> useCounterMap = new HashMap<String, Integer>();

  private static long fetchTimeout = TimeInMillis.SECOND * 10;

  private static long lastFetchTime = 0;

  public static GWikiProps readUseCounter(GWikiContext wikiContext)
  {
    FileSystem fs = wikiContext.getWikiWeb().getStorage().getFileSystem();
    if (fs.exists(PageViewCounterfile) == false) {
      return new GWikiProps();
    }
    String text = fs.readTextFile(PageViewCounterfile);
    return new GWikiProps(PropUtils.toProperties(text));
  }

  public static void writeUseCounter(GWikiContext wikiContext, GWikiProps props)
  {
    FileSystem fs = wikiContext.getWikiWeb().getStorage().getFileSystem();
    String data = PropUtils.fromProperties(props.getMap());
    String name = FileNameUtils.getParentDir(PageViewCounterfile);
    fs.mkdirs(name);
    fs.writeTextFile(PageViewCounterfile, data, true);
  }

  public static void persist(GWikiContext wikiContext, Map<String, Integer> pers)
  {
    GWikiProps props = readUseCounter(wikiContext);
    for (Map.Entry<String, Integer> me : pers.entrySet()) {
      props.setIntValue(me.getKey(), props.getIntValue(me.getKey(), 0) + me.getValue());
    }
    writeUseCounter(wikiContext, props);
    lastReadUseCounterMap = props;
  }

  public static synchronized int getUseCounter(GWikiContext wikiContext, String pageId)
  {
    int res = 0;
    res = lastReadUseCounterMap.getIntValue(pageId, 0);
    Integer i = useCounterMap.get(pageId);
    if (i != null) {
      res += i;
    }
    return res;
  }

  /**
   * create a copy
   * 
   * @return
   */
  public static Map<String, Integer> getUseCounters()
  {
    GWikiProps props = lastReadUseCounterMap;
    Map<String, Integer> ret = new HashMap<String, Integer>();
    synchronized (GWikiUseCounterFilter.class) {
      if (useCounterMap != null) {
        ret.putAll(useCounterMap);
      }
    }
    if (props != null) {
      for (String me : props.getMap().keySet()) {
        int v = props.getIntValue(me, 0);
        Integer p = ret.get(me);
        if (p != null) {
          ret.put(me, p + v);
        } else {
          ret.put(me, v);
        }
      }
    }
    return ret;
  }

  public static void increment(GWikiContext wikiContext, String pageId)
  {
    Map<String, Integer> backup = null;
    synchronized (GWikiUseCounterFilter.class) {
      Integer c = useCounterMap.get(pageId);
      if (c == null) {
        c = new Integer(0);
      }
      useCounterMap.put(pageId, c + 1);
      long now = System.currentTimeMillis();
      if (lastFetchTime + fetchTimeout < now) {
        lastFetchTime = now;
        backup = useCounterMap;
        useCounterMap = new HashMap<String, Integer>();
      }
    }
    if (backup != null) {
      persist(wikiContext, backup);
    }
  }

  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain, GWikiServeElementFilterEvent event)
  {
    if (event.getElement().getElementInfo().isIndexed() == true) {
      increment(event.getWikiContext(), event.getElement().getElementInfo().getId());
    }
    return chain.nextFilter(event);
  }

}

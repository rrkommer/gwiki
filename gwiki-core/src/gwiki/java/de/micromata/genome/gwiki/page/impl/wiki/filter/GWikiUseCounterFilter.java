/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.12.2009
// Copyright Micromata 09.12.2009
//
/////////////////////////////////////////////////////////////////////////////
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

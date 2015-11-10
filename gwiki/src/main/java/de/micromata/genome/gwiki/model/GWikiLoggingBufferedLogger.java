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
package de.micromata.genome.gwiki.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.types.Pair;

/**
 * Logger instance which caches recent log messages and delegated them to the configured logger instance
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class GWikiLoggingBufferedLogger extends GWikiLoggingBase implements GWikiLogViewer
{

  private GWikiLogging targetLogger;

  private Queue<GWikiLogEntry> cachedLogEntries;

  private int cacheSize = DEFAULT_CACHE_SIZE;

  private GWikiLogLevel logThreshold = DEFAULT_LOG_THRESHOLD;

  long lastAccessTimestamp = 0L;

  private static final int DEFAULT_CACHE_SIZE = 1000;

  private static final GWikiLogLevel DEFAULT_LOG_THRESHOLD = GWikiLogLevel.NOTE;

  private static final long ACCESS_SLEEP_TIME = 1000;

  boolean inLogging = false;

  /**
   * 
   */
  public GWikiLoggingBufferedLogger(GWikiLogging targetLogger)
  {
    this.targetLogger = targetLogger;
    cachedLogEntries = new LinkedList<GWikiLogEntry>();
  }

  public void reinitConfig()
  {
    long currentTime = System.currentTimeMillis();
    lastAccessTimestamp = currentTime - ACCESS_SLEEP_TIME;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiLogging#doLog(de.micromata.genome.gwiki.model.GWikiLogLevel, java.lang.String,
   * de.micromata.genome.gwiki.page.GWikiContext, java.lang.Throwable, java.lang.Object[])
   */
  public void doLog(GWikiLogLevel logLevel, String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    cacheLogMessage(logLevel, message, ex, ctx, keyValues);
    targetLogger.doLog(logLevel, message, ctx, ex, keyValues);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiLogging#doLog(de.micromata.genome.gwiki.model.GWikiLogLevel, java.lang.String,
   * de.micromata.genome.gwiki.page.GWikiContext, java.lang.Object[])
   */
  public void doLog(GWikiLogLevel logLevel, String message, GWikiContext ctx, Object... keyValues)
  {
    if (cacheLogMessage(logLevel, message, null, ctx, keyValues) == true) {
      targetLogger.doLog(logLevel, message, ctx, keyValues);
    }
  }

  /**
   * @param logLevel
   * @param message
   * @param ex
   * @param keyValues
   */
  private boolean cacheLogMessage(GWikiLogLevel logLevel, String message, Throwable ex, GWikiContext ctx, Object... keyValues)
  {
    GWikiLogEntry logEntry = new GWikiLogEntry(logLevel, message, keyValues, ex);
    GWikiLogLevel threshold = getLogThreshold(ctx);

    synchronized (cachedLogEntries) {
      if (logLevel.getPriority() < threshold.getPriority() == true) {
        return false;
      }

      cachedLogEntries.offer(logEntry);

      // shrink cache to maximal size
      int currentCacheSize = getCacheSize(ctx);
      while (cachedLogEntries.size() > currentCacheSize) {
        cachedLogEntries.poll();
      }
    }
    return true;
  }

  /**
   * @param ctx
   */
  private int getCacheSize(GWikiContext ctx)
  {
    if (ctx == null || inLogging == true) {
      return cacheSize;
    }

    long currentTime = System.currentTimeMillis();
    if (lastAccessTimestamp + ACCESS_SLEEP_TIME > currentTime) {
      return cacheSize;
    }

    lastAccessTimestamp = currentTime;
    inLogging = true;
    try {
      if (GWikiWeb.get() != null && ctx.getWikiWeb().getDaoContext().getPluginRepository().isPluginActive("gwiki.admintools") == true) {
        GWikiProps props = ctx.getElementFinder().getConfigProps("admin/config/GWikiLogViewerConfig");
        cacheSize = props.getIntValue("LOGVIEWER_NUM_ENTRIES_TO_CACHE", cacheSize);
      }
      return cacheSize;
    } finally {
      inLogging = false;
    }
  }

  /**
   * @param ctx
   */
  private GWikiLogLevel getLogThreshold(GWikiContext ctx)
  {
    if (ctx == null || inLogging == true) {
      return logThreshold;
    }

    long currentTime = System.currentTimeMillis();
    if (lastAccessTimestamp + ACCESS_SLEEP_TIME > currentTime) {
      return logThreshold;
    }

    lastAccessTimestamp = currentTime;
    inLogging = true;
    try {
      if (ctx.getWikiWeb().getDaoContext().getPluginRepository().isPluginActive("gwiki.admintools") == true) {
        GWikiProps props = ctx.getElementFinder().getConfigProps("admin/config/GWikiLogViewerConfig");
        logThreshold = GWikiLogLevel.valueOf(props.getStringValue("LOGVIEWER_LEVEL_THRESHOLD", logThreshold.name()));
      }

      return logThreshold;
    } finally {
      inLogging = false;
    }
  }

  /**
   * @param pointName
   * @param millis
   * @param wait
   * @see de.micromata.genome.gwiki.model.GWikiLogging#addPerformance(java.lang.String, long, long)
   */
  public void addPerformance(String pointName, long millis, long wait)
  {
    targetLogger.addPerformance(pointName, millis, wait);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiLogViewer#grep(java.util.Date, java.util.Date, java.lang.Integer, java.lang.String,
   * java.util.List, int, int, de.micromata.genome.gwiki.model.GWikiLogViewer.Callback)
   */
  public void grep(final Date start, final Date end, final GWikiLogLevel logLevel, final String message,
      final List<Pair<String, String>> params, int offset, int maxitems, Callback cb)
  {

    ArrayList<GWikiLogEntry> filteredEntries;

    synchronized (cachedLogEntries) {
      filteredEntries = (ArrayList<GWikiLogEntry>) CollectionUtils.select(cachedLogEntries, new Predicate<GWikiLogEntry>() {

        public boolean evaluate(GWikiLogEntry entry)
        {
          // Startdatum
          if (start != null) {
            if (entry.getDate().before(start)) {
              return false;
            }
          }

          // Enddatum
          if (end != null) {
            if (entry.getDate().after(end)) {
              return false;
            }
          }

          // LogLevel
          if (logLevel != null && entry.getLogLevel().getPriority() < logLevel.getPriority()) {
            return false;
          }

          if (message != null) {
            if (entry.getMessage().toLowerCase().contains(message.toLowerCase()) == false) {
              return false;
            }
          }

          if (params != null && params.isEmpty() == false) {
            for (Pair<String, String> searchParam : params) {
              boolean found = false;
              for (Pair<String, String> logParam : entry.getParamMap()) {
                if (logParam.getKey().equalsIgnoreCase(searchParam.getKey())) {
                  if (logParam.getValue().contains(searchParam.getValue())) {
                    found = true;
                  }
                }
              }

              // param not found
              if (found == false) {
                return false;
              }
            }
          }

          return true;
        }
      });
    }

    // sort to date
    Collections.sort(filteredEntries, new Comparator<GWikiLogEntry>() {
      public int compare(GWikiLogEntry o1, GWikiLogEntry o2)
      {
        return o1.getDate().after(o2.getDate()) ? -1 : 1;
      }
    });

    if (filteredEntries.size() > offset) {
      int i = offset;
      int endIndex = offset + maxitems;
      while (i < endIndex && filteredEntries.size() > i) {
        cb.found(filteredEntries.get(i));
        ++i;
      }
    }
  }

  @Override
  public boolean isDebugEnabled()
  {
    return logThreshold.getPriority() <= GWikiLogLevel.DEBUG.getPriority();
  }

  @Override
  public boolean isInfoEnabled()
  {
    return logThreshold.getPriority() <= GWikiLogLevel.INFO.getPriority();
  }

}

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

import org.apache.commons.lang.ObjectUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogAttribute;
import de.micromata.genome.logging.LogAttributeTypeWrapper;
import de.micromata.genome.logging.LogExceptionAttribute;
import de.micromata.genome.logging.LogLevel;
import de.micromata.genome.logging.LoggingServiceManager;

/**
 * Adapter from gwiki logging to genome logging.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GenomeLogging implements GWikiLogging
{

  /**
   * Gets the log attributes.
   *
   * @param ctx the ctx
   * @param ex the ex
   * @param keyValues the key values
   * @return the log attributes
   */
  protected LogAttribute[] getLogAttributes(GWikiContext ctx, Throwable ex, Object[] keyValues)
  {
    int attrSize = keyValues.length / 2;
    if (ex != null) {
      ++attrSize;
    }
    LogAttribute[] attrs = new LogAttribute[attrSize];
    int ai = 0;
    for (int i = 0; i < keyValues.length; ++i) {
      // Object k = keyValues[i];
      // if ((k instanceof GLogAttributeName) == false) {
      // ++i;
      // continue;
      // }
      // GLogAttributeName kn = (GLogAttributeName) k;
      String k = ObjectUtils.toString(keyValues[i]);
      ++i;
      if (i >= keyValues.length) {
        break;
      }

      String v = ObjectUtils.toString(keyValues[i]);
      LogAttribute la = new LogAttribute(new LogAttributeTypeWrapper(k), v);
      attrs[ai] = la;
      ++ai;
    }
    if (ex != null) {
      attrs[attrSize - 1] = new LogExceptionAttribute(ex);
    }
    return attrs;
  }

  @Override
  public boolean isDebugEnabled()
  {
    return GLog.isDebugEnabled();
  }

  @Override
  public boolean isInfoEnabled()
  {
    return GLog.isInfoEnabled();
  }

  /**
   * Convert.
   *
   * @param ll the ll
   * @return the log level
   */
  protected LogLevel convert(GWikiLogLevel ll)
  {
    switch (ll) {
      case DEBUG:
        return LogLevel.Debug;
      case ERROR:
        return LogLevel.Error;
      case FATAL:
        return LogLevel.Fatal;
      case INFO:
        return LogLevel.Info;
      case NOTE:
        return LogLevel.Note;
      case WARN:
        return LogLevel.Warn;
      default:
        return LogLevel.Error;
    }
  }

  @Override
  public void doLog(GWikiLogLevel logLevel, String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    doLog(convert(logLevel), message, ctx, ex, keyValues);

  }

  @Override
  public void doLog(GWikiLogLevel logLevel, String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(convert(logLevel), message, ctx, keyValues);
  }

  @Override
  public void reinitConfig()
  {
  }

  /**
   * Do log.
   *
   * @param ll the ll
   * @param message the message
   * @param ctx the ctx
   * @param keyValues the key values
   */
  protected void doLog(LogLevel ll, String message, GWikiContext ctx, Object... keyValues)
  {
    GLog.doLog(ll, GWikiLogCategory.Wiki, message, getLogAttributes(ctx, null, keyValues));
  }

  /**
   * Do log.
   *
   * @param ll the ll
   * @param message the message
   * @param ctx the ctx
   * @param ex the ex
   * @param keyValues the key values
   */
  protected void doLog(LogLevel ll, String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    GLog.doLog(ll, GWikiLogCategory.Wiki, message, getLogAttributes(ctx, ex, keyValues));
  }

  @Override
  public void debug(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(LogLevel.Debug, message, ctx, keyValues);
  }

  @Override
  public void error(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(LogLevel.Error, message, ctx, keyValues);
  }

  @Override
  public void fatal(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(LogLevel.Fatal, message, ctx, keyValues);
  }

  @Override
  public void info(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(LogLevel.Info, message, ctx, keyValues);
  }

  @Override
  public void note(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(LogLevel.Note, message, ctx, keyValues);
  }

  @Override
  public void warn(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(LogLevel.Warn, message, ctx, keyValues);
  }

  @Override
  public void note(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    doLog(LogLevel.Note, message, ctx, ex, keyValues);
  }

  @Override
  public void warn(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    doLog(LogLevel.Warn, message, ctx, ex, keyValues);
  }

  @Override
  public void error(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    doLog(LogLevel.Error, message, ctx, ex, keyValues);
  }

  @Override
  public void fatal(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    doLog(LogLevel.Error, message, ctx, ex, keyValues);
  }

  @Override
  public void addPerformance(String pointName, long millis, long wait)
  {
    LoggingServiceManager.get().getStatsDAO().addPerformance(GWikiLogCategory.Wiki, pointName, millis, wait);
  }

}

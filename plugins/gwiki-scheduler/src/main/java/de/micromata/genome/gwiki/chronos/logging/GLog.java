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
package de.micromata.genome.gwiki.chronos.logging;

import de.micromata.genome.gwiki.model.GWikiLog;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GLog
{
  public static void doLog(LogLevel logLevel, LogCategory cat, String msg, LogAttribute... attributes)
  {
    switch (logLevel) {
      case Debug:
        debug(cat, msg, attributes);
        break;
      case Error:
        error(cat, msg, attributes);
        break;
      case Warn:
        warn(cat, msg, attributes);
        break;
      case Fatal:
        error(cat, msg, attributes);
        break;
      case Note:
        note(cat, msg, attributes);
        break;
      case Trace:
        debug(cat, msg, attributes);
        break;
    }
    // try {
    // GenomeDaoManager.get().getLogging().doLog(logLevel, cat, msg, attributes);
    // } catch (Exception ex) {
    // fallBackLogging.doLog(logLevel, cat, msg, attributes);
    // }
  }

  public static void debug(LogCategory cat, String msg, LogAttribute... attributes)
  {
    GWikiLog.debug(cat.name() + "; " + msg);
    // try {
    // GenomeDaoManager.get().getLogging().debug(cat, msg, attributes);
    // } catch (Exception ex) {
    // fallBackLogging.debug(cat, msg, attributes);
    // }
  }

  public static void trace(LogCategory cat, String msg, LogAttribute... attributes)
  {
    GWikiLog.debug(cat.name() + "; " + msg);
    // try {
    // GenomeDaoManager.get().getLogging().trace(cat, msg, attributes);
    // } catch (Exception ex) {
    // fallBackLogging.trace(cat, msg, attributes);
    // }
  }

  public static void info(LogCategory cat, String msg, LogAttribute... attributes)
  {
    GWikiLog.info(cat.name() + "; " + msg);
    // try {
    // GenomeDaoManager.get().getLogging().info(cat, msg, attributes);
    // } catch (Exception ex) {
    // fallBackLogging.debug(cat, msg, attributes);
    // }
  }

  public static void note(LogCategory cat, String msg, LogAttribute... attributes)
  {
    GWikiLog.note(cat.name() + "; " + msg);
    // try {
    // GenomeDaoManager.get().getLogging().note(cat, msg, attributes);
    // } catch (Exception ex) {
    // fallBackLogging.debug(cat, msg, attributes);
    // }
  }

  public static void warn(LogCategory cat, String msg, LogAttribute... attributes)
  {
    GWikiLog.warn(cat.name() + "; " + msg);
    // try {
    // GenomeDaoManager.get().getLogging().note(cat, msg, attributes);
    // } catch (Exception ex) {
    // fallBackLogging.debug(cat, msg, attributes);
    // }
  }

  // public static void warn(LogCategory cat, String msg, LogAttribute... attributes)
  // {
  // try {
  // GenomeDaoManager.get().getLogging().warn(cat, msg, attributes);
  // } catch (Exception ex) {
  // fallBackLogging.debug(cat, msg, attributes);
  // }
  // }

  public static void error(LogCategory cat, String msg, LogAttribute... attributes)
  {
    GWikiLog.error(cat.name() + "; " + msg);
    // try {
    // GenomeDaoManager.get().getLogging().error(cat, msg, attributes);
    // } catch (Exception ex) {
    // fallBackLogging.debug(cat, msg, attributes);
    // }
  }

  public static void warn(LogCategory cat, String msg)
  {
    GWikiLog.warn(cat.name() + "; " + msg);
  }

  public static void warn(LogCategory cat, String msg, Throwable ex)
  {
    GWikiLog.warn(cat.name() + "; " + msg, ex);
  }

  public static void error(LogCategory cat, String msg, Throwable ex)
  {
    GWikiLog.error(cat.name() + "; " + msg, ex);
  }

  public static void error(LogCategory cat, String msg)
  {
    GWikiLog.error(cat.name() + "; " + msg);
  }

  // public static void fatal(LogCategory cat, String msg, LogAttribute... attributes)
  // {
  // try {
  // GenomeDaoManager.get().getLogging().fatal(cat, msg, attributes);
  // } catch (Exception ex) {
  // fallBackLogging.debug(cat, msg, attributes);
  // }
  // }

  // public static boolean isLogEnabled(LogLevel ll)
  // {
  // // return GWikiLog.i
  // return GenomeDaoManager.get().getLogConfigurationDAO().isLogEnabled(ll);
  // }

  // public static boolean isLogEnabled(LogLevel logLevel, String fqCategory, String msg)
  // {
  // return GenomeDaoManager.get().getLogConfigurationDAO().isLogEnabled(logLevel, fqCategory, msg);
  // }

  public static boolean isDebugEnabled(LogCategory logCat, String msg)
  {
    // return isDebugEnabled(logCat.getFqName(), msg);
    return false;
  }

  public static boolean isDebugEnabled(String fqCategory, String msg)
  {
    // return isLogEnabled(LogLevel.Debug, fqCategory, msg);
    return false;
  }

  public static boolean isTraceEnabled(LogCategory logCat, String msg)
  {
    // return isTraceEnabled(logCat.getFqName(), msg);
    return false;
  }

  public static boolean isTraceEnabled(String fqCategory, String msg)
  {
    // return isLogEnabled(LogLevel.Trace, fqCategory, msg);
    return false;
  }

  public static boolean isDebugEnabled()
  {
    return false;// isLogEnabled(LogLevel.Debug);
  }

  public static boolean isNoteEnabled()
  {
    return true;// isLogEnabled(LogLevel.Note);
  }

  public static boolean isInfoEnabled()
  {
    return false;// isLogEnabled(LogLevel.Info);
  }

  public static boolean isTraceEnabled()
  {
    return false;// isLogEnabled(LogLevel.Trace);
  }
}

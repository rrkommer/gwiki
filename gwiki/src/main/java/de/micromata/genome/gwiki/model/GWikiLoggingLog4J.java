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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * GWikiLogging implementing writing to Log4J.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiLoggingLog4J extends GWikiLoggingBase
{
  private static final Logger log = Logger.getLogger(GWikiLoggingLog4J.class);

  public void addPerformance(String pointName, long millis, long wait)
  {

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiLogging#doLog(de.micromata.genome.gwiki.model.GWikiLogLevel, java.lang.String,
   * de.micromata.genome.gwiki.page.GWikiContext, java.lang.Throwable, java.lang.Object[])
   */
  public void doLog(GWikiLogLevel logLevel, String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    log.log(mapToLog4jPriority(logLevel), renderLog(message, ctx, ex, keyValues));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiLogging#doLog(de.micromata.genome.gwiki.model.GWikiLogLevel, java.lang.String,
   * de.micromata.genome.gwiki.page.GWikiContext, java.lang.Object[])
   */
  public void doLog(GWikiLogLevel logLevel, String message, GWikiContext ctx, Object... keyValues)
  {
    log.log(mapToLog4jPriority(logLevel), renderLog(message, ctx, null, keyValues));
  }

  /**
   * @param logLevel
   * @return
   */
  private Priority mapToLog4jPriority(GWikiLogLevel logLevel)
  {
    switch (logLevel) {
      case DEBUG:
        return Level.DEBUG;
      case NOTE:
        return Level.INFO;
      case INFO:
        return Level.INFO;
      case WARN:
        return Level.WARN;
      case ERROR:
        return Level.ERROR;
      case FATAL:
        return Level.FATAL;
      default:
        return Level.INFO;
    }
  }

  @Override
  public boolean isDebugEnabled()
  {
    return log.isDebugEnabled();
  }

  @Override
  public boolean isInfoEnabled()
  {
    return log.isInfoEnabled();
  }

}

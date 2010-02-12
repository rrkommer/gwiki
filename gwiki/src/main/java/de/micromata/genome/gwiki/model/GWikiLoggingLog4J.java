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

import org.apache.log4j.Logger;

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

  public void debug(String message, GWikiContext ctx, Object... keyValues)
  {
    log.debug(renderLog(message, ctx, null, keyValues));
  }

  public void error(String message, GWikiContext ctx, Object... keyValues)
  {
    log.error(renderLog(message, ctx, null, keyValues));

  }

  public void error(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    log.error(renderLog(message, ctx, ex, keyValues));

  }

  public void fatal(String message, GWikiContext ctx, Object... keyValues)
  {
    log.fatal(renderLog(message, ctx, null, keyValues));

  }

  public void fatal(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    log.fatal(renderLog(message, ctx, ex, keyValues));
  }

  public void info(String message, GWikiContext ctx, Object... keyValues)
  {
    log.info(renderLog(message, ctx, null, keyValues));
  }

  public void note(String message, GWikiContext ctx, Object... keyValues)
  {
    log.info(renderLog(message, ctx, null, keyValues));

  }

  public void note(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    log.info(renderLog(message, ctx, ex, keyValues));
  }

  public void warn(String message, GWikiContext ctx, Object... keyValues)
  {
    log.warn(renderLog(message, ctx, null, keyValues));
  }

  public void warn(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    log.warn(renderLog(message, ctx, ex, keyValues));
  }

}

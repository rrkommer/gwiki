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

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Simple logging interface.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiLogging
{
  boolean isDebugEnabled();

  boolean isInfoEnabled();

  /**
   * 
   * @param message message of the log
   * @param ctx Context of log
   * @param keyValues expect pairs of GLogAttributeName and Object with provides toString()
   */
  void debug(String message, GWikiContext ctx, Object... keyValues);

  void info(String message, GWikiContext ctx, Object... keyValues);

  void note(String message, GWikiContext ctx, Object... keyValues);

  void warn(String message, GWikiContext ctx, Object... keyValues);

  void error(String message, GWikiContext ctx, Object... keyValues);

  void fatal(String message, GWikiContext ctx, Object... keyValues);

  void note(String message, GWikiContext ctx, Throwable ex, Object... keyValues);

  void warn(String message, GWikiContext ctx, Throwable ex, Object... keyValues);

  void error(String message, GWikiContext ctx, Throwable ex, Object... keyValues);

  void fatal(String message, GWikiContext ctx, Throwable ex, Object... keyValues);

  void doLog(GWikiLogLevel logLevel, String message, GWikiContext ctx, Throwable ex, Object... keyValues);

  void doLog(GWikiLogLevel logLevel, String message, GWikiContext ctx, Object... keyValues);

  /**
   * Adds a performance entry
   * 
   * @param pointName Name of the measurement
   * @param millis time spend in milli seconds
   * @param wait time spend in waiting.
   */
  public void addPerformance(String pointName, long millis, long wait);

  /**
   * Just load configuration again.
   */
  public void reinitConfig();
}

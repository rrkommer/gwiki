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

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author stefans
 *
 */
public abstract class GWikiLoggingAdapter implements GWikiLogging
{

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#debug(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Object[])
   */
  public void debug(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(GWikiLogLevel.DEBUG, message, ctx, keyValues);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#info(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Object[])
   */
  public void info(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(GWikiLogLevel.INFO, message, ctx, keyValues);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#note(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Object[])
   */
  public void note(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(GWikiLogLevel.NOTE, message, ctx, keyValues);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#warn(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Object[])
   */
  public void warn(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(GWikiLogLevel.WARN, message, ctx, keyValues);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#error(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Object[])
   */
  public void error(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(GWikiLogLevel.ERROR, message, ctx, keyValues);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#fatal(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Object[])
   */
  public void fatal(String message, GWikiContext ctx, Object... keyValues)
  {
    doLog(GWikiLogLevel.FATAL, message, ctx, keyValues);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#note(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Throwable, java.lang.Object[])
   */
  public void note(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    doLog(GWikiLogLevel.NOTE, message, ctx, ex, keyValues);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#warn(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Throwable, java.lang.Object[])
   */
  public void warn(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    doLog(GWikiLogLevel.WARN, message, ctx, ex, keyValues);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#error(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Throwable, java.lang.Object[])
   */
  public void error(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    doLog(GWikiLogLevel.ERROR, message, ctx, ex, keyValues);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiLogging#fatal(java.lang.String, de.micromata.genome.gwiki.page.GWikiContext, java.lang.Throwable, java.lang.Object[])
   */
  public void fatal(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    doLog(GWikiLogLevel.FATAL, message, ctx, ex, keyValues);
  }
}
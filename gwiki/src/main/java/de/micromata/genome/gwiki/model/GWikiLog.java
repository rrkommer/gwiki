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
 * Static helper function delegates all calles to GWikiLogging.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiLog
{
  static boolean inGetGWikiWeb = false;

  protected static GWikiWeb getWikiWeb(GWikiContext ctx)
  {
    if (inGetGWikiWeb == true) {
      return null;
    }
    try {
      inGetGWikiWeb = true;
      if (ctx != null) {
        return ctx.getWikiWeb();
      }
      return GWikiWeb.getWiki();
    } finally {
      inGetGWikiWeb = false;
    }
  }

  protected static GWikiLogging getLogging(GWikiContext ctx)
  {
    GWikiWeb wiki = getWikiWeb(ctx);
    if (wiki == null) {
      return new GenomeLogging();
    }
    return wiki.getLogging();
  }

  public static void warn(String message)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    getLogging(ctx).warn(message, ctx);
  }

  public static void error(String message)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    getLogging(ctx).error(message, ctx);
  }

  public static void warn(String message, Throwable ex)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    getLogging(ctx).warn(message, ctx, ex);
  }

  public static void error(String message, Throwable ex)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    getLogging(ctx).error(message, ctx, ex);
  }

  public static void doLog(GWikiLogLevel logLevel, String message, Throwable ex, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    getLogging(ctx).doLog(logLevel, message, ctx, ex, keyValues);
  }

  public static void doLog(GWikiLogLevel logLevel, String message, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    getLogging(ctx).doLog(logLevel, message, ctx, keyValues);
  }
}

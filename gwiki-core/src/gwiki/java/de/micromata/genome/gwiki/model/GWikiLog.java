/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   05.12.2009
// Copyright Micromata 05.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Static helper function delegates all calles to GWikiLogging.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiLog
{
  public static void debug(String message, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().debug(message, ctx, keyValues);
  }

  public static void info(String message, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().info(message, ctx, keyValues);
  }

  public static void note(String message, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().note(message, ctx, keyValues);
  }

  public static void warn(String message, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().warn(message, ctx, keyValues);
  }

  public static void error(String message, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().error(message, ctx, keyValues);
  }

  public static void fatal(String message, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().fatal(message, ctx, keyValues);
  }

  public static void note(String message, Throwable ex, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().note(message, ctx, ex, keyValues);
  }

  public static void warn(String message, Throwable ex, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().warn(message, ctx, ex, keyValues);
  }

  public static void error(String message, Throwable ex, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().error(message, ctx, ex, keyValues);
  }

  public static void fatal(String message, Throwable ex, Object... keyValues)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    ctx.getWikiWeb().getLogging().fatal(message, ctx, ex, keyValues);
  }
}

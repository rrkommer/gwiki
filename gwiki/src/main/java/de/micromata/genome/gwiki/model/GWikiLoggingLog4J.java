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

import org.apache.log4j.Logger;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * GWikiLogging implementing writing to Log4J.
 * 
 * @author roger
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

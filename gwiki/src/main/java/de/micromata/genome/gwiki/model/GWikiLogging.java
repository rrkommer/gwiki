/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.11.2009
// Copyright Micromata 06.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Simple logging interface.
 *  
 * @author roger@micromata.de
 * 
 */
public interface GWikiLogging
{
  /**
   * 
   * @param message
   * @param ctx
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

  public void addPerformance(String pointName, long millis, long wait);
}

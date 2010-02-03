/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   07.12.2009
// Copyright Micromata 07.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiContext;

public interface GWikiSchedulerProvider
{
  /**
   * start async job. If job cannot be started, because scheduler is bussy, returns false. An error message will be added to wikiContext
   * 
   * @return
   */
  boolean execAsyncOne(GWikiContext wikiContext, Class< ? extends GWikiSchedulerJob> callback, Map<String, String> args);

  /**
   * Execute multiple instances of job
   * 
   * @param wikiContext
   * @param callback
   * @param args
   * @return
   */
  boolean execAsyncMultiple(final GWikiContext wikiContext, final Class< ? extends GWikiSchedulerJob> callback,
      final Map<String, String> args);
}

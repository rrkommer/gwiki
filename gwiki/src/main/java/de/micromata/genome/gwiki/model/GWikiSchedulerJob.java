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

import java.io.Serializable;
import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Interface for a job executed in a thread.
 * 
 * @author roger
 * 
 */
public interface GWikiSchedulerJob extends Serializable
{
  public Object call(Map<String, String> args);

  public void call();

  public GWikiContext getWikiContext();

  public void setWikiContext(GWikiContext wikiContext);

  public Map<String, String> getArgs();

  public void setArgs(Map<String, String> args);

}

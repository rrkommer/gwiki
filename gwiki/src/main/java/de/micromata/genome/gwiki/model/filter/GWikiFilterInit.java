/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.01.2010
// Copyright Micromata 09.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.filter;

import de.micromata.genome.gwiki.model.GWikiWeb;

/**
 * If a GWikiFilter also implement this method, it will be called after construction.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiFilterInit
{
  public void init(GWikiWeb wikiWeb);
}

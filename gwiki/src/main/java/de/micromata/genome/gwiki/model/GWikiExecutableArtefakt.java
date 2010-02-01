/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Artefakt can display/render to html.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiExecutableArtefakt<T extends Serializable> extends GWikiArtefakt<T>
{
  /**
   * Will be called before page will be rendered.
   * 
   * Usefull to add required css/or js or set http header.
   * 
   * @param wikiContext
   */
  public void prepareHeader(GWikiContext wikiContext);

  /**
   * 
   * @param ctx
   * @return true if continue processing. Otherwise stop processing.
   */
  public boolean render(GWikiContext wikiContext);
}

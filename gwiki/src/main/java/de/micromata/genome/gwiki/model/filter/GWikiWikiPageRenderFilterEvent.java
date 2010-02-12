/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   17.11.2009
// Copyright Micromata 17.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.filter;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;

/**
 * Event for a GWikiWikiPageRenderFilter.
 * 
 * @author roger
 * 
 */
public class GWikiWikiPageRenderFilterEvent extends GWikiFilterEvent
{
  protected GWikiWikiPageArtefakt wikiPageArtefakt;

  public GWikiWikiPageRenderFilterEvent(GWikiContext wikiContext, GWikiWikiPageArtefakt wikiPageArtefakt)
  {
    super(wikiContext);
    this.wikiPageArtefakt = wikiPageArtefakt;
  }

  public GWikiWikiPageArtefakt getWikiPageArtefakt()
  {
    return wikiPageArtefakt;
  }

  public void setWikiPageArtefakt(GWikiWikiPageArtefakt wikiPageArtefakt)
  {
    this.wikiPageArtefakt = wikiPageArtefakt;
  }

}

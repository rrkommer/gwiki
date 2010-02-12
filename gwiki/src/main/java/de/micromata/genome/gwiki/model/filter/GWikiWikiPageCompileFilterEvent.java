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

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;

/**
 * Event for a GWikiWikiPageCompileFilter.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiPageCompileFilterEvent extends GWikiFilterEvent
{
  protected GWikiElement element;

  protected GWikiWikiPageArtefakt wikiPageArtefakt;

  public GWikiWikiPageCompileFilterEvent(GWikiContext wikiContext, GWikiElement element, GWikiWikiPageArtefakt wikiPageArtefakt)
  {
    super(wikiContext);
    this.element = element;
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

  public GWikiElement getElement()
  {
    return element;
  }

  public void setElement(GWikiElement element)
  {
    this.element = element;
  }

}

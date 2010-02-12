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

/**
 * Event with a containing GWikiElement.
 * 
 * @author roger
 * 
 */
public class GWikiElementFilterEvent extends GWikiFilterEvent
{
  protected GWikiElement element;

  public GWikiElementFilterEvent(GWikiContext wikiContext, GWikiElement element)
  {
    super(wikiContext);
    this.element = element;
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

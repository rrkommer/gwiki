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

import java.util.Map;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @see GWikiStorageDeleteElementFilter
 * @author roger@micromata.de
 * 
 */
public class GWikiStorageDeleteElementFilterEvent extends GWikiStorageElementPartsFilterEvent
{

  public GWikiStorageDeleteElementFilterEvent(GWikiContext wikiContext, GWikiElement element, Map<String, GWikiArtefakt< ? >> parts)
  {
    super(wikiContext, element, parts);
  }

}

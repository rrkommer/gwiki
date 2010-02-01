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

public class GWikiStorageStoreElementFilterEvent extends GWikiStorageElementPartsFilterEvent
{
  public GWikiStorageStoreElementFilterEvent(GWikiContext wikiContext, GWikiElement element, Map<String, GWikiArtefakt< ? >> parts)
  {
    super(wikiContext, element, parts);
  }

}

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

public class GWikiStorageElementPartsFilterEvent extends GWikiElementFilterEvent
{
  protected Map<String, GWikiArtefakt< ? >> parts;

  public GWikiStorageElementPartsFilterEvent(GWikiContext wikiContext, GWikiElement element, Map<String, GWikiArtefakt< ? >> parts)
  {
    super(wikiContext, element);
    this.parts = parts;
  }

  public Map<String, GWikiArtefakt< ? >> getParts()
  {
    return parts;
  }

  public void setParts(Map<String, GWikiArtefakt< ? >> parts)
  {
    this.parts = parts;
  }

}

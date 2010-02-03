/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.12.2009
// Copyright Micromata 18.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.filter;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilterEvent;

public class GWikiWriteAccessStoreElementFilter implements GWikiStorageStoreElementFilter
{

  public Void filter(GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter> chain,
      GWikiStorageStoreElementFilterEvent event)
  {
    String id = event.getElement().getElementInfo().getId();
    if (event.getWikiContext().getWikiWeb().getWikiConfig().hasWriteAccess(event.getWikiContext(), id) == false) {
      throw new AuthorizationFailedException("Invalid path access: " + id);
    }
    return chain.nextFilter(event);
  }

}

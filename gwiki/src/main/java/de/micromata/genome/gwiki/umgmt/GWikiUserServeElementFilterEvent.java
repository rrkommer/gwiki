/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   01.12.2009
// Copyright Micromata 01.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.umgmt;

import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;

public class GWikiUserServeElementFilterEvent implements GWikiServeElementFilter
{
  public static ThreadLocal<GWikiSimpleUser> CURRENT_USER = new ThreadLocal<GWikiSimpleUser>();

  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain, GWikiServeElementFilterEvent event)
  {
    try {
      GWikiSimpleUser user = GWikiSimpleUserAuthorization.getSingleUser(event.getWikiContext());
      if (user != null) {
        CURRENT_USER.set(user);
      }
      return chain.nextFilter(event);
    } finally {
      CURRENT_USER.set(null);

    }
  }
}

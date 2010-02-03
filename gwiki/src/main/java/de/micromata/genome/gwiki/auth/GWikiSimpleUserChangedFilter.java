/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.01.2010
// Copyright Micromata 09.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.auth;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilter;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.umgmt.GWikiUserAuthorization;

/**
 * Filter to change current user lookalike.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiSimpleUserChangedFilter implements GWikiPageChangedFilter
{

  public Void filter(GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter> chain, GWikiPageChangedFilterEvent event)
  {
    final GWikiContext wikiContext = event.getWikiContext();
    if (wikiContext == null
        || event.getNewInfo() == null
        || event.getNewInfo().getMetaTemplate() == null
        || StringUtils.equals(event.getNewInfo().getMetaTemplate().getPageId(), "admin/templates/intern/WikiUserMetaTemplate") == false) {
      return chain.nextFilter(event);
    }
    GWikiSimpleUser su = GWikiSimpleUserAuthorization.getSingleUser(wikiContext);
    if (su == null) {
      return chain.nextFilter(event);
    }
    String id = event.getNewInfo().getId();
    String user = su.getUser();
    if (id.endsWith("/" + user) == false) {
      return chain.nextFilter(event);
    }
    GWikiSimpleUser nus = new GWikiUserAuthorization().findUser(wikiContext, user);
    if (nus == null) {
      return chain.nextFilter(event);
    }
    GWikiSimpleUserAuthorization.setSingleUser(wikiContext, nus);
    return chain.nextFilter(event);
  }
}

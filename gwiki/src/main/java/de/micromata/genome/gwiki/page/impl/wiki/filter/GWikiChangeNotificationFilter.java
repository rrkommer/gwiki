/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   08.12.2009
// Copyright Micromata 08.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiChangeNotificationFilter implements GWikiStorageStoreElementFilter
{

  public static void findUser(GWikiContext wikiContext, Properties props, GWikiElementInfo ei, boolean recursiveOnly, Set<String> ret)
  {
    Map<String, Boolean> m = GWikiChangeNotificationActionBean.getNotificationEmailsForPage(wikiContext, ei.getId(), props);
    for (Map.Entry<String, Boolean> me : m.entrySet()) {
      if (recursiveOnly == true && me.getValue() != Boolean.TRUE) {
        continue;
      }
      ret.add(me.getKey());
    }
    ei = ei.getParent(wikiContext);
    if (ei == null) {
      return;
    }
    findUser(wikiContext, props, ei, true, ret);
  }

  public Void filter(GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter> chain,
      GWikiStorageStoreElementFilterEvent event)
  {
    final GWikiContext wikiContext = event.getWikiContext();
    chain.nextFilter(event);
    if (wikiContext.getBooleanRequestAttribute(GWikiEditPageActionBean.NO_NOTIFICATION_EMAILS) == true) {
      return null;
    }
    GWikiElementInfo ei = event.getElement().getElementInfo();
    String id = ei.getId();
    Properties props = GWikiChangeNotificationActionBean.getNotificationEmails(wikiContext);
    Set<String> userNames = new HashSet<String>();
    findUser(event.getWikiContext(), props, ei, false, userNames);
    if (userNames.isEmpty() == true) {
      return null;
    }
    Map<String, String> args = new HashMap<String, String>();
    args.put("pageId", id);
    wikiContext.getWikiWeb().getSchedulerProvider()
        .execAsyncMultiple(wikiContext, GWikichangeNotificationEmailSendSchedulerJob.class, args);
    return null;
  }
}

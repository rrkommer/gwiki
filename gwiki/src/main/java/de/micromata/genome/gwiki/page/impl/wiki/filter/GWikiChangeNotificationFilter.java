////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

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

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiChangeNotificationFilter implements GWikiStorageStoreElementFilter
{

  public static void findUser(GWikiContext wikiContext, Properties props, GWikiElementInfo ei, boolean recursiveOnly,
      Set<String> ret)
  {
    Map<String, Boolean> m = GWikiChangeNotificationActionBean.getNotificationEmailsForPage(wikiContext, ei.getId(),
        props);
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

  @Override
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

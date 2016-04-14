//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.umgmt;

import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Event for a GWikiServeElementFilter.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUserServeElementFilterEvent implements GWikiServeElementFilter
{
  // TODO put this in Authorization
  public static ThreadLocal<GWikiSimpleUser> CURRENT_USER = new ThreadLocal<GWikiSimpleUser>();

  public static GWikiSimpleUser setUser(GWikiSimpleUser user)
  {
    GWikiSimpleUser prev = CURRENT_USER.get();
    CURRENT_USER.set(user);
    return prev;
  }

  public static GWikiSimpleUser getUser()
  {
    return CURRENT_USER.get();
  }

  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain, GWikiServeElementFilterEvent event)
  {
    GWikiContext wikiContext = event.getWikiContext();
    try {
      wikiContext.getWikiWeb().getAuthorization().initThread(wikiContext);
      return chain.nextFilter(event);
    } finally {
      wikiContext.getWikiWeb().getAuthorization().clearThread(wikiContext);
      setUser(null);

    }
  }
}

////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

package de.micromata.genome.gwiki.umgmt;

import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;

/**
 * Event for a GWikiServeElementFilter.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUserServeElementFilterEvent implements GWikiServeElementFilter
{
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
    try {
      GWikiSimpleUser user = GWikiSimpleUserAuthorization.getSingleUser(event.getWikiContext());
      if (user != null) {
        setUser(user);
      }
      return chain.nextFilter(event);
    } finally {
      setUser(null);

    }
  }
}

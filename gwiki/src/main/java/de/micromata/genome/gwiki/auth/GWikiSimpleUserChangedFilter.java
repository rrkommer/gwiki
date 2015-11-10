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

package de.micromata.genome.gwiki.auth;

import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilter;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Filter to change current user lookalike.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSimpleUserChangedFilter implements GWikiPageChangedFilter
{

  public Void filter(GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter> chain, GWikiPageChangedFilterEvent event)
  {
    final GWikiContext wikiContext = event.getWikiContext();
    wikiContext.getWikiWeb().getAuthorization().reloadUser(wikiContext);
    // if (wikiContext == null
    // || event.getNewInfo() == null
    // || event.getNewInfo().getMetaTemplate() == null
    // || StringUtils.equals(event.getNewInfo().getMetaTemplate().getPageId(), "admin/templates/intern/WikiUserMetaTemplate") == false) {
    // return chain.nextFilter(event);
    // }
    // GWikiSimpleUser su = GWikiSimpleUserAuthorization.getSingleUser(wikiContext);
    // if (su == null) {
    // return chain.nextFilter(event);
    // }
    // String id = event.getNewInfo().getId();
    // String user = su.getUser();
    // if (id.endsWith("/" + user) == false) {
    // return chain.nextFilter(event);
    // }
    // GWikiSimpleUser nus = new GWikiUserAuthorization().findUser(wikiContext, user);
    // if (nus == null) {
    // return chain.nextFilter(event);
    // }
    // GWikiSimpleUserAuthorization.setSingleUser(wikiContext, nus);
    return chain.nextFilter(event);
  }
}

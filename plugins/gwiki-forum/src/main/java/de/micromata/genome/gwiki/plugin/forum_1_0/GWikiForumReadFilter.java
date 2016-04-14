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

package de.micromata.genome.gwiki.plugin.forum_1_0;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import de.micromata.genome.gwiki.model.GWikiAuthorization.UserPropStorage;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiForumReadFilter implements GWikiServeElementFilter
{
  public static final String GWIKI_READ_PAGES = "GWIKI_READ_PAGES";

  protected void track(GWikiContext wikiContext, GWikiElementInfo ei)
  {
    if (wikiContext.getWikiWeb().getAuthorization().needAuthorization(wikiContext) == true) {
      return;
    }
    if (ei.isIndexed() == false) {
      return;
    }
    if (ei.getMetaTemplate() != null && ei.getMetaTemplate().isNoSearchIndex() == true) {
      return;
    }
    // GWikiForumTrackReadContainer wikiCo
    GWikiForumTrackReadContainer rc = new GWikiForumTrackReadContainer(
        wikiContext.getWikiWeb().getAuthorization().getUserProp(wikiContext,
            GWIKI_READ_PAGES));
    if (rc.markRead(ei) == false) {
      return;
    }

    String queryS = "prop:MODIFIEDAT > " + rc.getLastReadS();
    SearchQuery query = new SearchQuery(queryS, wikiContext.getWikiWeb());

    QueryResult sr = wikiContext.getWikiWeb().getDaoContext().getContentSearcher().search(wikiContext, query);
    if (sr.getFoundItems() == 0) {
      rc.setReadedPages((Set) Collections.emptySet());
      rc.setLastRead(new Date().getTime());
    }
    wikiContext.getWikiWeb().getAuthorization().setUserProp(wikiContext, GWIKI_READ_PAGES, rc.toString(),
        UserPropStorage.Client);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  @Override
  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain,
      GWikiServeElementFilterEvent event)
  {
    track(event.getWikiContext(), event.getElement().getElementInfo());
    return chain.nextFilter(event);
  }

}

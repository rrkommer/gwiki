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

package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiSpaceFilter implements GWikiServeElementFilter
{
  public static final String WIKI_SET_SPACE_PARAM = "_wikispaceset";

  @Override
  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain,
      GWikiServeElementFilterEvent event)
  {
    GWikiContext wikiContext = event.getWikiContext();
    String setspace = wikiContext.getRequestParameter(WIKI_SET_SPACE_PARAM);
    if (StringUtils.isNotBlank(setspace) == true) {
      String redid = wikiContext.getWikiWeb().getSpaces().switchUserSpace(wikiContext, setspace);
      if (redid != null) {
        try {
          wikiContext.getResponse().sendRedirect(wikiContext.localUrl(redid));
          return null;
          //          wikiContext.sendError(HttpServletResponse.SC_TEMPORARY_REDIRECT, redUrl);
        } catch (IOException ex) {
          ; // nothing
        }
      }
    } else {
      String spaceId = wikiContext.getWikiWeb().getSpaces().findCurrentPageSpaceId(wikiContext);
      if (spaceId != null && StringUtils.equals(spaceId, setspace) == false) {
        String redid = wikiContext.getWikiWeb().getSpaces().switchUserSpace(wikiContext, spaceId);
      }
    }
    chain.nextFilter(event);

    return null;
  }

}

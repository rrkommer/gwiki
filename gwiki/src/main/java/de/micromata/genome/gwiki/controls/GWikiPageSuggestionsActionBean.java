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

package de.micromata.genome.gwiki.controls;

import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.expr.SearchUtils;

/**
 * ActionBean for Ajax page autocompletion.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageSuggestionsActionBean extends GWikiPageListActionBean
{

  public GWikiPageSuggestionsActionBean()
  {

  }

  @Override
  public Object onInit()
  {
    return onLinkAutocomplete();
  }

  public Object onLinkAutocomplete()
  {
    String q = wikiContext.getRequestParameter("q");
    String pageType = wikiContext.getRequestParameter("pageType");
    String queryexpr = SearchUtils.createLinkExpression(q, true, pageType);
    // String queryexpr = "prop:PAGEID ~ \"" + q + "\" or prop:TITLE ~ \"" + q + "\"";
    // if (StringUtils.isNotEmpty(pageType) == true) {
    // if (pageType.equals("image") == true) {
    // pageType = "attachment";
    // }
    // queryexpr = "prop:TYPE = " + pageType + " and (" + queryexpr + ")";
    // }
    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb());

    query.setMaxCount(1000);
    QueryResult qr = filter(query);
    StringBuilder sb = new StringBuilder();
    // int size = qr.getResults().size();
    for (SearchResult sr : qr.getResults()) {
      sb.append(sr.getPageId()).append("|").append(wikiContext.getTranslatedProp(sr.getElementInfo().getTitle())).append("\n");
    }
    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

}

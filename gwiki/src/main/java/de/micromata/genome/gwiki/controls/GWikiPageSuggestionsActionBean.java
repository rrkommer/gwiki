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

package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

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

  /**
   * deprecated
   * 
   * @return
   */
  public Object onLinkAutocomplete()
  {
    String format = wikiContext.getRequestParameter("f");
    String q = wikiContext.getRequestParameter("q");
    String pageType = wikiContext.getRequestParameter("pageType");
    String queryexpr = SearchUtils.createLinkExpression(q, true, pageType);
    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb());

    query.setMaxCount(1000);
    QueryResult qr = filter(query);
    StringBuilder sb = new StringBuilder();
    // int size = qr.getResults().size();
    if (StringUtils.equals(format, "json") == true) {
      wikiContext.getResponse().setContentType("application/json");
      sb.append("[");
      boolean first = true;
      for (SearchResult sr : qr.getResults()) {
        if (first == false) {
          sb.append(",");
        } else {
          first = false;
        }
        String tti = wikiContext.getTranslatedProp(sr.getElementInfo().getTitle());
        sb.append("{ pageId: '").append(sr.getPageId()).append("', title: '")
            .append(StringEscapeUtils.unescapeJavaScript(tti)).append("'}");
        //        sb.append("'").append(sr.getPageId()).append("'");
      }
      sb.append("]");
    } else {
      for (SearchResult sr : qr.getResults()) {
        sb.append(sr.getPageId()).append("|").append(wikiContext.getTranslatedProp(sr.getElementInfo().getTitle()))
            .append("\n");
      }
    }
    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

}

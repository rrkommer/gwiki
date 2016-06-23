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

package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.eclipsesource.json.JsonArray;

import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.expr.SearchUtils;
import de.micromata.genome.gwiki.utils.JsonBuilder;
import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * Ajax ActionBean for searching.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSearchBoxActionBean extends GWikiPageListActionBean
{
  private String pageId;

  private String pageType;

  public GWikiSearchBoxActionBean()
  {

  }

  @Override
  public Object onInit()
  {
    return onLinkAutocomplete();
  }

  public void renderSearchOps(JsonArray ret, String query)
  {
    String searchUrl = wikiContext.localUrl("/edit/Search") + "?method_onSearch=go&se="
        + WebUtils.encodeUrlParam(query);

    ret.add(JsonBuilder.map("key", searchUrl, "label",
        translate("gwiki.nav.searchbox.localsearch")));
    if (StringUtils.isNotBlank(pageId) == true) {
      searchUrl += "&childs=" + WebUtils.encodeUrlParam(pageId);
      ret.add(JsonBuilder.map("key", searchUrl,
          "label", translate("gwiki.nav.searchbox.globalsearch")));
    }

  }

  public Object onLinkAutocomplete()
  {
    String q = StringUtils.trim(wikiContext.getRequestParameter("q"));
    String pageType = wikiContext.getRequestParameter("pageType");
    String queryexpr = SearchUtils.createLinkExpression(q, false, pageType);
    JsonArray jsonarr = JsonBuilder.array();
    renderSearchOps(jsonarr, q);

    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb());

    query.setMaxCount(1000);
    query.setWithSampleText(false);

    QueryResult qr = filter(query);

    StringBuilder sb = new StringBuilder();
    // int size = qr.getResults().size();

    for (SearchResult sr : qr.getResults()) {
      if (sr.getElementInfo().isViewable() == false) {
        continue;
      }
      String t = wikiContext.getTranslatedProp(StringEscapeUtils.escapeHtml(sr.getElementInfo().getTitle()))
          + "<br/>("
          + StringEscapeUtils.escapeHtml(sr.getPageId())
          + ")";
      jsonarr.add(JsonBuilder.map("key", sr.getPageId(), "label", t));
    }
    String ret = jsonarr.toString();
    wikiContext.getResponse().setContentType("application/json");
    wikiContext.append(ret);
    wikiContext.flush();
    return noForward();
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getPageType()
  {
    return pageType;
  }

  public void setPageType(String pageType)
  {
    this.pageType = pageType;
  }

}

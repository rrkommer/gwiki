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

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParser;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.expr.SearchUtils;
import de.micromata.genome.gwiki.utils.JsonBuilder;
import de.micromata.genome.gwiki.utils.JsonBuilder.JsonArray;
import de.micromata.genome.gwiki.utils.JsonBuilder.JsonMap;

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

  public Object onWeditAutocomplete()
  {
    // {, !, [
    String format = wikiContext.getRequestParameter("c");
    String querystring = wikiContext.getRequestParameter("q");
    JsonMap resp = null;
    if (StringUtils.length(format) != 1) {
      resp = JsonBuilder.map("ret", 10, "message", "No type given");
    } else {
      JsonArray array = JsonBuilder.array();
      switch (format.charAt(0)) {
        case '!':
          fillImageLinks(querystring, array);
          break;
        case '[':
          fillPageLinks(querystring, array);
          break;
        case '{':
          fillMacroLinks(querystring, array);
          break;

        case 'x':
          array.add(JsonBuilder.map("label", "Erster!", "key", "first"));
          array.add(JsonBuilder.map("label", "Zweiter!", "key", "second"));
          break;
        default:
          resp = JsonBuilder.map("ret", 11, "message", "Unknown type: " + format);
          break;
      }
      if (resp == null) {
        resp = JsonBuilder.map("ret", 0, "list", array);
      }
    }

    String json = resp.toString();
    try {
      wikiContext.getResponseOutputStream().write(json.getBytes("UTF-8"));
      wikiContext.getResponseOutputStream().flush();
      return noForward();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

  }

  private void fillMacroLinks(String querystring, JsonArray array)
  {
    Map<String, GWikiMacroFactory> mfm = wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext);
    for (Map.Entry<String, GWikiMacroFactory> me : mfm.entrySet()) {
      array.add(JsonBuilder.map("key", me.getKey() + "}", "label", me.getKey()));
    }

  }

  private void fillPageLinks(String querystring, JsonArray array)
  {
    String pageType = "gwiki";
    String queryexpr = SearchUtils.createLinkExpression(querystring, true, pageType);
    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb());

    query.setMaxCount(1000);
    QueryResult qr = filter(query);
    for (SearchResult sr : qr.getResults()) {
      String pageid = sr.getPageId();
      array.add(JsonBuilder.map("key", pageid + "]", "label",
          wikiContext.getTranslatedProp(sr.getElementInfo().getTitle())));
    }

  }

  private void fillImageLinks(String querystring, JsonArray array)
  {
    String pageType = "";
    String queryexpr = SearchUtils.createLinkExpression(querystring, true, pageType);
    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb());

    query.setMaxCount(1000);
    QueryResult qr = filter(query);
    for (SearchResult sr : qr.getResults()) {
      String pageid = sr.getPageId();
      if (StringUtils.endsWithAny(pageid, new String[] { ".png", ".jpeg", ".PNG", ".JPEG", ".JPG", ".jpg" }) == false) {
        continue;
      }
      array.add(JsonBuilder.map("key", sr.getPageId() + "!", "label",
          wikiContext.getTranslatedProp(sr.getElementInfo().getTitle())));
    }
  }
}

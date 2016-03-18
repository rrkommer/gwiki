package de.micromata.genome.gwiki.controls;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.parser.WeditWikiUtils;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.expr.SearchUtils;
import de.micromata.genome.gwiki.utils.JsonBuilder;
import de.micromata.genome.gwiki.utils.JsonBuilder.JsonArray;
import de.micromata.genome.gwiki.utils.JsonBuilder.JsonMap;

/**
 * Ajax services.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiWeditServiceActionBean extends ActionBeanAjaxBase
{
  private String txt;

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
    return sendStringResponse(json);

  }

  private void fillMacroLinks(String querystring, JsonArray array)
  {
    Map<String, GWikiMacroFactory> mfm = wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext);
    for (Map.Entry<String, GWikiMacroFactory> me : mfm.entrySet()) {

      JsonMap map = JsonBuilder.map("key", me.getKey(), "label", me.getKey());
      GWikiMacroFactory fac = me.getValue();
      map.put("macro_withbody", fac.hasBody());
      map.put("macro_rtemacro", fac.isRteMacro());
      map.put("macro_evalbody", fac.evalBody());

      map.put("onInsert", "gwedit_insert_macro");
      array.add(map);
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
      String titel = wikiContext.getTranslatedProp(sr.getElementInfo().getTitle());
      array.add(JsonBuilder.map(
          "key", pageid,
          "title", titel,
          "label",
          StringEscapeUtils.escapeHtml(titel) + "<br/><small>("
              + StringEscapeUtils.escapeHtml(pageid) + ")</small>",
          "onInsert", "gwedit_insert_acpagelink"));
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
      String title = wikiContext.getTranslatedProp(sr.getElementInfo().getTitle());
      array.add(JsonBuilder.map("key", sr.getPageId(), "label",
          title, "onInsert", "gwedit_insert_imagelink"));
    }
  }

  protected QueryResult filter(SearchQuery query)
  {
    query.setFindUnindexed(true);
    QueryResult qr = wikiContext.getWikiWeb().getContentSearcher().search(wikiContext, query);
    return qr;
  }

  public Object onWikiToWedit()
  {

    String text = WeditWikiUtils.wikiToWedit(txt);
    JsonMap resp = JsonBuilder.map("ret", 0, "text", text);
    String ret = resp.toString();
    return sendStringResponse(ret);
  }

  public Object onWeditToWiki()
  {

    String text = WeditWikiUtils.weditToWiki(txt);
    JsonMap resp = JsonBuilder.map("ret", 0, "text", text);
    String ret = resp.toString();
    return sendStringResponse(ret);
  }

  public String getTxt()
  {
    return txt;
  }

  public void setTxt(String txt)
  {
    this.txt = txt;
  }

}

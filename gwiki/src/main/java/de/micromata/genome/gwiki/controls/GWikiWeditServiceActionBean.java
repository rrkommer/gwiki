package de.micromata.genome.gwiki.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import de.micromata.genome.gwiki.model.GWikiAuthorization.UserPropStorage;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.parser.WeditWikiUtils;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.expr.SearchUtils;
import de.micromata.genome.gwiki.utils.JsonBuilder;
import de.micromata.genome.util.types.Pair;

/**
 * Ajax services.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiWeditServiceActionBean extends ActionBeanAjaxBase
{

  public static final String GWIKI_DEFAULT_EDITOR = "gwikidefeditor";
  private String txt;
  private String macro;
  private String macroHead;
  private String macroBody;

  public Object onWeditAutocomplete()
  {
    // {, !, [
    String format = wikiContext.getRequestParameter("c");
    String querystring = wikiContext.getRequestParameter("q");
    JsonObject resp = null;
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

    return sendResponse(resp);

  }

  public Object onGetMacroInfos()
  {
    JsonArray array = JsonBuilder.array();
    fillMacroLinks("", array);
    JsonObject resp = JsonBuilder.map("ret", 0, "list", array);
    return sendResponse(resp);
  }

  /**
   * Returns a MacroInfo (see js)
   * 
   * @return
   */
  public Object onGetMacroInfo()
  {
    Map<String, GWikiMacroFactory> mfm = wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext);
    GWikiMacroFactory fac = mfm.get(macro);
    if (fac == null) {
      JsonObject resp = JsonBuilder.map("ret", 10, "message", "Unknown macro: " + macro);
      return sendResponse(resp);
    }
    JsonObject macroInfo = JsonBuilder.map("macroName", macro, "macroHead", macro);
    JsonObject resp = JsonBuilder.map("ret", 0, "macroInfo", macroInfo);
    JsonObject info = fillMacroInfo(macro, fac);
    macroInfo.set("macroMetaInfo", info);
    if (StringUtils.isNotBlank(macroHead) == true) {
      macroInfo.set("macroHead", macroHead);
      MacroAttributes ma = new MacroAttributes();
      ma.parse(macroHead);
      macroInfo.set("macroName", ma.getCmd());
      JsonArray jparams = JsonBuilder.array();
      for (Map.Entry<String, String> me : ma.getArgs().getMap().entrySet()) {
        jparams.add(JsonBuilder.map("name", me.getKey(), "value", me.getValue()));
      }
      macroInfo.set("macroParams", jparams);
    }
    return sendResponse(resp);
  }

  private void fillMacroLinks(String querystring, JsonArray array)
  {
    Map<String, GWikiMacroFactory> mfm = wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext);
    List<String> macroNames = new ArrayList<>(mfm.keySet());
    Collections.sort(macroNames);
    for (String macroName : macroNames) {
      GWikiMacroFactory fac = mfm.get(macroName);
      if (fac.isRteMacro() == true) {
        continue;
      }
      JsonObject map = JsonBuilder.map("key", macroName, "label", macroName);
      map.set("onInsert", "gwedit_insert_macro");
      fillMacroInfo(macroName, fac);
      map.set("macroMetaInfo", fillMacroInfo(macroName, fac));
      array.add(map);
    }

  }

  private JsonObject fillMacroInfo(String macroName, GWikiMacroFactory fac)
  {
    GWikiMacroInfo mInfo = fac.getMacroInfo();
    List<MacroParamInfo> params = mInfo.getParamInfos();
    JsonArray jp = JsonBuilder.array();
    for (MacroParamInfo pi : params) {
      jp.add(JsonBuilder.map("name", pi.getName(),
          "type", pi.getType().name(),
          "required", pi.isRequired(),
          "defaultValue", pi.getDefaultValue(),
          "info", pi.getInfo()));

    }
    Pair<String, String> templ = mInfo.getRteTemplate(macroName);
    JsonObject ret = JsonBuilder.map("info", mInfo.getInfo(),
        "macroName", macroName,
        "hasBody", mInfo.hasBody(),
        "evalBody", mInfo.evalBody(),
        "rteMacro", mInfo.isRteMacro(),
        "macroTemplateBegin", templ.getFirst(),
        "macroTemplateEnd", templ.getSecond(),
        "macroParams", jp);
    return ret;
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

  @Deprecated
  public Object onWikiToWedit()
  {

    String text = WeditWikiUtils.wikiToWedit(txt);
    JsonObject resp = JsonBuilder.map("ret", 0, "text", text);
    String ret = resp.toString();
    return sendStringResponse(ret);
  }

  @Deprecated
  public Object onWeditToWiki()
  {

    String text = WeditWikiUtils.weditToWiki(txt);
    JsonObject resp = JsonBuilder.map("ret", 0, "text", text);
    String ret = resp.toString();
    return sendStringResponse(ret);
  }

  public Object onWikiToRte()
  {
    String rte = WeditWikiUtils.wikiToRte(wikiContext, txt);
    return sendStringResponse(rte);
  }

  public Object onRteToWiki()
  {
    String wiki = WeditWikiUtils.rteToWiki(wikiContext, txt);
    return sendStringResponse(wiki);
  }

  public Object onSetDefaultEditorType()
  {
    String editorType = (String) wikiContext.getRequestAttribute("editorType");
    editorType = StringUtils.defaultString(editorType, "wiki");
    wikiContext.getWikiWeb().getAuthorization().setUserProp(wikiContext, GWIKI_DEFAULT_EDITOR, editorType,
        UserPropStorage.Client);
    return noForward();
  }

  public String getTxt()
  {
    return txt;
  }

  public void setTxt(String txt)
  {
    this.txt = txt;
  }

  public String getMacro()
  {
    return macro;
  }

  public void setMacro(String macro)
  {
    this.macro = macro;
  }

  public String getMacroHead()
  {
    return macroHead;
  }

  public void setMacroHead(String macroHead)
  {
    this.macroHead = macroHead;
  }

  public String getMacroBody()
  {
    return macroBody;
  }

  public void setMacroBody(String macroBody)
  {
    this.macroBody = macroBody;
  }

}

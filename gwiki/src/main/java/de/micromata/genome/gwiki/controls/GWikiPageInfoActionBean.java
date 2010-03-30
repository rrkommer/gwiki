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

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.code;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.element;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.a;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.br;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.li;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.nbsp;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.table;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.th;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.ul;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragementLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.XmlNode;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * ActionBean to show information about a page.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageInfoActionBean extends ActionBeanBase implements GWikiPropKeys
{
  private Map<String, String> infoBoxen = new HashMap<String, String>();

  private String pageId;

  private GWikiElementInfo elementInfo;

  private String restoreId;

  private String[] compareVersions;

  /**
   * used for renaming.
   */
  private String newPageId;

  /**
   * Comma seperated list of box elements to show. if null or empty, show all.
   */
  private String[] showBoxElements = new String[0];

  private String getDisplayDate(Date date)
  {
    if (date == null)
      return "unknown";
    return wikiContext.getUserDateString(date);
  }

  public static XmlElement getBoxFrame(String boxName, XmlNode... nested)
  {
    XmlElement el = element("div", attrs("class", "panel"),//
        element("label", element("b", text(boxName))));
    el.nest(nested);
    return el;
  }

  public static XmlElement getStandardTable()
  {
    return table(attrs("border", "1", "cellspacing", "0", "cellpadding", "2"));
  }

  public static List<String> getOutgoingLinks(GWikiContext wikiContext, GWikiWikiPageArtefakt artefakt)
  {
    artefakt.compileFragements(wikiContext);
    GWikiContent c = artefakt.getCompiledObject();

    if (c == null) {
      return Collections.emptyList();
    }
    final List<String> ret = new ArrayList<String>();
    c.iterate(new GWikiFragmentVisitor() {

      public void begin(GWikiFragment fragment)
      {
        if (fragment instanceof GWikiFragementLink) {
          ret.add(((GWikiFragementLink) fragment).getTarget());
        }
      }

      public void end(GWikiFragment fragment)
      {
        // nothing
      }
    });
    return ret;
  }

  public static List<String> getOutgoingLinks(GWikiContext wikiContext, GWikiArtefakt< ? > artefakt)
  {

    if (artefakt instanceof GWikiWikiPageArtefakt) {
      return getOutgoingLinks(wikiContext, (GWikiWikiPageArtefakt) artefakt);
    }
    return Collections.emptyList();
  }

  public static Map<String, List<String>> getOutgoingLinks(GWikiContext wikiContext, GWikiElement elems)
  {
    Map<String, List<String>> ret = new HashMap<String, List<String>>();
    Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
    elems.collectParts(map);
    for (Map.Entry<String, GWikiArtefakt< ? >> me : map.entrySet()) {
      ret.put(me.getKey(), getOutgoingLinks(wikiContext, me.getValue()));
    }
    return ret;
  }

  protected String buildOutgoingLinks()
  {
    GWikiElement elems = wikiContext.getWikiWeb().getElement(elementInfo);
    Map<String, List<String>> outGoings = getOutgoingLinks(wikiContext, elems);
    XmlElement ta = ul(attrs());
    for (Map.Entry<String, List<String>> me : outGoings.entrySet()) {
      for (String url : me.getValue()) {
        ta.nest(li(code(wikiContext.renderLocalUrl(url))));
      }
    }
    return getBoxFrame(translate("gwiki.page.edit.PageInfo.outlinks.title"), ta).toString();
  }

  public String buildAttachmentsBox()
  {
    // wikiContext.g
    // StringBuilder sb = new StringBuilder();
    List<GWikiElementInfo> childs = wikiContext.getElementFinder().getPageAttachments(elementInfo.getId());
    XmlNode addNode = nbsp();
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToCreate(wikiContext, elementInfo) == true) {
      addNode = a(attrs("href", wikiContext.localUrl("/edit/EditPage")
          + "?newPage=true&parentPageId="
          + elementInfo.getId()
          + "&metaTemplatePageId=admin/templates/FileWikiPageMetaTemplate"), text("New Attachment"));
    }
    String backUrlParam = "backUrl=" + WebUtils.encodeUrlParam(wikiContext.localUrl("edit/pageInfo") + "?pageId=" + elementInfo.getId());
    XmlElement ta = getStandardTable();
    ta.nest(//
        tr(//
            th(text(translate("gwiki.page.edit.PageInfo.attachment.label.name"))), //
            th(text(translate("gwiki.page.edit.PageInfo.attachment.label.size"))), //
            th(text(translate("gwiki.page.edit.PageInfo.attachment.label.version"))), //
            th(text(translate("gwiki.page.edit.PageInfo.attachment.label.action"))) //
        )//
        );
    for (GWikiElementInfo ce : childs) {

      ta.nest(//
          tr(//
              td(code(wikiContext.renderLocalUrl(ce.getId()))), //
              td(text(ce.getProps().getStringValue(GWikiPropKeys.SIZE, "-1"))), //
              td(text(translate("gwiki.page.edit.PageInfo.attachment.label.versioninfo", wikiContext.getUserDateString(ce.getModifiedAt()),
                  ce.getModifiedBy()))),

              td(//
                  a(attrs("href", wikiContext.localUrl("/edit/EditPage") + "?pageId=" + ce.getId() + "&" + backUrlParam),
                      text(translate("gwiki.page.edit.PageInfo.attachment.label.edit"))),//
                  // br(), //
                  a(attrs("href", wikiContext.localUrl("/edit/EditPage")
                      + "?pageId="
                      + ce.getId()
                      + "&method_onDelete=true&"
                      + backUrlParam), text(translate("gwiki.page.edit.PageInfo.attachment.label.delete")), //
                      // br(), //
                      a(attrs("onclick", "return confirm('"
                          + translate("gwiki.page.edit.PageInfo.attachment.message.deleteconfirm")
                          + "');", "href", wikiContext.localUrl("/edit/PageInfo") + "?pageId=" + ce.getId()),
                          text(translate("gwiki.page.edit.PageInfo.attachment.label.info")))) //
              )//
          ) //
          );

    }

    return getBoxFrame(translate("gwiki.page.edit.PageInfo.attachment.title"), addNode, ta).toString();
  }

  protected String buildBaseInfo()
  {
    XmlElement ta = getStandardTable();
    XmlNode changeId = code("");
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToEdit(wikiContext, elementInfo) == true
        && wikiContext.getWikiWeb().getAuthorization().isAllowToCreate(wikiContext, elementInfo) == true
        && wikiContext.isAllowTo(GWikiAuthorizationRights.GWIKI_DELETEPAGES.name())) {
      changeId = element("div", //
          element("form", //
              element("input", attrs("type", "text", "size", "50", "value", StringEscapeUtils.escapeXml(elementInfo.getId()), "name",
                  "newPageId")), br(), //
              element("input", attrs("type", "submit", "class", "gwikiButton main", "name", "method_onRename", "value",
                  translate("gwiki.page.edit.PageInfo.info.button.rename"))),//
              element("script", code("")) //
          ));
    }
    ta.nest(//
        tr(//
            th(text(translate("gwiki.page.edit.PageInfo.info.label.pageId"))),// 
            td(text(elementInfo.getId()), changeId) //
        ), //
        tr(//
            th(text(translate("gwiki.page.edit.PageInfo.info.label.title"))),// 
            td(text(wikiContext.getTranslatedProp(elementInfo.getTitle()))) //
        ), //
        tr( //
            th(text(translate("gwiki.page.edit.PageInfo.info.label.author"))), //
            td(text(elementInfo.getProps().getStringValue(CREATEDBY)))//
        ), //
        tr(//
            th(text(translate("gwiki.page.edit.PageInfo.info.label.createdat"))), //
            td(text(getDisplayDate(elementInfo.getProps().getDateValue(CREATEDAT)))) //
        ), //
        tr( //
            th(text(translate("gwiki.page.edit.PageInfo.info.label.modifiedby"))), //
            td(text(elementInfo.getProps().getStringValue(MODIFIEDBY)))//
        ), //
        tr(//
            th(text(translate("gwiki.page.edit.PageInfo.info.label.modifiedat"))), //
            td(text(getDisplayDate(elementInfo.getProps().getDateValue(MODIFIEDAT)))) //
        ) //
        );
    return getBoxFrame(translate("gwiki.page.edit.PageInfo.info.title"), ta).toString();
  }

  protected String loadVersionInfos()
  {
    List<GWikiElementInfo> versionInfos = wikiContext.getWikiWeb().getVersions(elementInfo);

    Collections.sort(versionInfos, new Comparator<GWikiElementInfo>() {

      public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
      {
        return o2.getId().compareTo(o1.getId());
      }
    });
    versionInfos.add(0, elementInfo);
    // versionInfos.add(0, elementInfo);
    XmlElement cmd = element("input", attrs("type", "submit", "class", "gwikiButton main", "name", "method_onCompare", //
        "value", translate("gwiki.page.edit.PageInfo.version.button.compare")));

    XmlElement ta = getStandardTable();
    ta.nest(//
        tr(//
            th(code("&nbsp;")), //
            th(text(translate("gwiki.page.edit.PageInfo.version.label.author"))), //
            th(text(translate("gwiki.page.edit.PageInfo.version.label.time"))), //
            th(text(translate("gwiki.page.edit.PageInfo.version.label.action"))) //
        )//
        );
    for (GWikiElementInfo ei : versionInfos) {

      ta.nest(//
          tr(//
              td(code("<input type=\"checkbox\" name=\"compareVersions\" value=\"" + ei.getId() + "\"")), //
              td(text(StringUtils.defaultString(ei.getProps().getStringValue(MODIFIEDBY), "Unknown"))), //
              td(text(getDisplayDate(ei.getProps().getDateValue(MODIFIEDAT)))), //
              td(//
                  a(attrs("href", wikiContext.localUrl(ei.getId())), text(translate("gwiki.page.edit.PageInfo.version.button.view"))), //
                  ei == elementInfo ? nbsp() : a(attrs("href", wikiContext.localUrl("edit/PageInfo")
                      + "?restoreId="
                      + ei.getId()
                      + "&pageId="
                      + pageId
                      + "&method_onRestore=true"), //
                      text(translate("gwiki.page.edit.PageInfo.version.button.restore"))//
                      )) //
          ));
    }
    XmlElement np = Html.p(cmd, Html.br(), ta);
    return getBoxFrame(translate("gwiki.page.edit.PageInfo.version.title"), np).toString();
  }

  protected boolean showInfo(String boxName)
  {
    if (showBoxElements == null || showBoxElements.length == 0) {
      return true;
    }
    return ArrayUtils.indexOf(showBoxElements, boxName) != -1;
  }

  protected void initialize()
  {
    if (StringUtils.isEmpty(pageId) == true) {
      wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.pageIdNotSet");
      return;
    }
    elementInfo = wikiContext.getWikiWeb().findElementInfo(pageId);
    if (elementInfo == null) {
      wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.cannotFindPageId", pageId);
      return;
    }
    if (showInfo("BaseInfo") == true) {
      infoBoxen.put("BaseInfo", buildBaseInfo());
    }
    if (showInfo("VersionInfo") == true) {
      infoBoxen.put("VersionInfo", loadVersionInfos());
    }
    if (showInfo("OutLinks") == true) {
      infoBoxen.put("OutLinks", buildOutgoingLinks());
    }
    if (showInfo("Attachments") == true) {
      infoBoxen.put("Attachments", buildAttachmentsBox());
    }
  }

  public Object onInit()
  {
    initialize();
    return null;
  }

  public Object onCancel()
  {
    return getWikiContext().getWikiWeb().findElement(pageId);

  }

  public Object onRestore()
  {
    initialize();
    if (StringUtils.isEmpty(restoreId) == true) {
      wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.noPageIdToRestore");
      return null;
    }
    GWikiElement rei = wikiContext.getWikiWeb().findElement(restoreId);
    if (rei == null) {
      wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.cannotFindRestorePageId", restoreId);
      return null;
    }
    wikiContext.getWikiWeb().restoreWikiPage(wikiContext, rei);

    return getWikiContext().getWikiWeb().findElement(pageId);
  }

  public Object onCompare()
  {
    if (compareVersions == null) {
      initialize();
      wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.noversionforcompare");
      return null;
    }
    if (compareVersions.length != 2) {
      initialize();
      wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.selecttwoversionsforcompare");
      return null;
    }
    String rd = "/edit/ComparePages"
        + "?leftPageId="
        + WebUtils.encodeUrlParam(compareVersions[0])
        + "&rightPageId="
        + WebUtils.encodeUrlParam(compareVersions[1])
        + "&backUrl="
        + WebUtils.encodeUrlParam(wikiContext.localUrl("/edit/PageInfo&pageId=") + this.pageId);
    return rd;
  }

  public Object onRename()
  {
    initialize();
    wikiContext.ensureAllowTo(GWikiAuthorizationRights.GWIKI_DELETEPAGES.name());
    List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChilds(elementInfo);
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToEdit(wikiContext, elementInfo) == false) {
      wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.norighttorename");
      return null;
    }
    if (StringUtils.isBlank(newPageId) == true) {
      wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.renamenonewpageid");
      return null;
    }
    GWikiElementInfo ti = wikiContext.getWikiWeb().findElementInfo(newPageId);
    if (ti != null) {
      wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.renameTargetExists", newPageId);
      return null;
    }
    for (GWikiElementInfo ci : childs) {
      if (wikiContext.getWikiWeb().getAuthorization().isAllowToEdit(wikiContext, ci) == false) {
        wikiContext.addValidationError("gwiki.page.edit.PageInfo.message.norighttochangechild", ci.getId());
        return null;
      }
    }
    GWikiElement el = wikiContext.getWikiWeb().loadNewElement(pageId);
    el.getElementInfo().setId(newPageId);
    // wikiContext.getWikiWeb().
    wikiContext.getWikiWeb().saveElement(wikiContext, el, true);
    for (GWikiElementInfo ci : childs) {
      GWikiElement cel = wikiContext.getWikiWeb().loadNewElement(ci.getId());
      cel.getElementInfo().getProps().setStringValue(GWikiPropKeys.PARENTPAGE, newPageId);
      wikiContext.getWikiWeb().saveElement(wikiContext, cel, true);
    }
    el = wikiContext.getWikiWeb().loadNewElement(pageId);
    wikiContext.getWikiWeb().getStorage().deleteElement(wikiContext, el);
    pageId = newPageId;
    infoBoxen.clear();
    elementInfo = null;
    initialize();
    return null;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public GWikiElementInfo getElementInfo()
  {
    return elementInfo;
  }

  public void setElementInfo(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

  public Map<String, String> getInfoBoxen()
  {
    return infoBoxen;
  }

  public void setInfoBoxen(Map<String, String> infoBoxen)
  {
    this.infoBoxen = infoBoxen;
  }

  public String getRestoreId()
  {
    return restoreId;
  }

  public void setRestoreId(String restoreId)
  {
    this.restoreId = restoreId;
  }

  public String[] getCompareVersions()
  {
    return compareVersions;
  }

  public void setCompareVersions(String[] compareVersions)
  {
    this.compareVersions = compareVersions;
  }

  public String getNewPageId()
  {
    return newPageId;
  }

  public void setNewPageId(String newPageId)
  {
    this.newPageId = newPageId;
  }

  public String[] getShowBoxElements()
  {
    return showBoxElements;
  }

  public void setShowBoxElements(String[] showBoxElements)
  {
    this.showBoxElements = showBoxElements;
  }
}

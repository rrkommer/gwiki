/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   23.10.2009
// Copyright Micromata 23.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.code;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.element;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.a;
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

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
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
 * @author roger@micromata.de
 * 
 */
public class GWikiPageInfoActionBean extends ActionBeanBase implements GWikiPropKeys
{
  private Map<String, String> infoBoxen = new HashMap<String, String>();

  private String pageId;

  private GWikiElementInfo elementInfo;

  private String restoreId;

  private String[] compareVersions;

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
    return getBoxFrame("Ausgehende Links", ta).toString();
  }

  protected String buildAttachmentsBox()
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
            th(text("Name")), //
            th(text("Size")), //
            th(text("Action")) //
        )//
        );
    for (GWikiElementInfo ce : childs) {

      ta.nest(//
          tr(//
              td(code(wikiContext.renderLocalUrl(ce.getId()))), //
              td(text(ce.getProps().getStringValue(GWikiPropKeys.SIZE, "-1"))), //
              td(//
                  a(attrs("href", wikiContext.localUrl("/edit/EditPage") + "?pageId=" + ce.getId() + "&" + backUrlParam), text("Edit")),//
                  // br(), //
                  a(attrs("href", wikiContext.localUrl("/edit/EditPage")
                      + "?pageId="
                      + ce.getId()
                      + "&method_onDelete=true&"
                      + backUrlParam), text("Delete")), //
                  // br(), //
                  a(attrs("href", wikiContext.localUrl("/edit/PageInfo") + "?pageId=" + ce.getId()), text("Info")) //
              )//
          ) //
          );

    }

    return getBoxFrame("Attachments", addNode, ta).toString();
  }

  protected String buildBaseInfo()
  {
    XmlElement ta = getStandardTable();
    ta.nest(//
        tr(//
            th(text("PageId:")),// 
            td(text(elementInfo.getId())) //
        ), //
        tr(//
            th(text("Titel:")),// 
            td(text(wikiContext.getTranslatedProp(elementInfo.getTitle()))) //
        ), //
        tr( //
            th(text("Autor:")), //
            td(text(elementInfo.getProps().getStringValue(CREATEDBY)))//
        ), //
        tr(//
            th(text("Erstellt:")), //
            td(text(getDisplayDate(elementInfo.getProps().getDateValue(CREATEDAT)))) //
        ), //
        tr( //
            th(text("Letzter Editor:")), //
            td(text(elementInfo.getProps().getStringValue(MODIFIEDBY)))//
        ), //
        tr(//
            th(text("Letzte Änderung:")), //
            td(text(getDisplayDate(elementInfo.getProps().getDateValue(MODIFIEDAT)))) //
        ) //
        );
    return getBoxFrame("Informationen", ta).toString();
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
    XmlElement cmd = element("input", attrs("type", "submit", "name", "method_onCompare", "value", "Compare"));

    XmlElement ta = getStandardTable();
    ta.nest(//
        tr(//
            th(code("&nbsp;")), //
            th(text("Author")), //
            th(text("Zeit")), //
            th(text("Action")) //
        )//
        );
    for (GWikiElementInfo ei : versionInfos) {

      ta.nest(//
          tr(//
              td(code("<input type=\"checkbox\" name=\"compareVersions\" value=\"" + ei.getId() + "\"")), //
              td(text(StringUtils.defaultString(ei.getProps().getStringValue(MODIFIEDBY), "Unknown"))), //
              td(text(getDisplayDate(ei.getProps().getDateValue(MODIFIEDAT)))), //
              td(//
                  a(attrs("href", wikiContext.localUrl(ei.getId())), text("Ansehen")), //
                  a(attrs("href", wikiContext.localUrl("edit/PageInfo")
                      + "?restoreId="
                      + ei.getId()
                      + "&pageId="
                      + pageId
                      + "&method_onRestore=true"), //
                      text("Wiederherstellen")//
                  )) //
          ));
    }
    XmlElement np = Html.p(cmd, Html.br(), ta);
    return getBoxFrame("Versionen", np).toString();
  }

  protected void initialize()
  {
    if (StringUtils.isEmpty(pageId) == true) {
      wikiContext.addSimpleValidationError("PageId not set");
      return;
    }
    elementInfo = wikiContext.getWikiWeb().findElementInfo(pageId);
    if (elementInfo == null) {
      wikiContext.addSimpleValidationError("Cannot find PageId: " + pageId);
      return;
    }
    infoBoxen.put("BaseInfo", buildBaseInfo());
    infoBoxen.put("VersionInfo", loadVersionInfos());
    infoBoxen.put("OutLinks", buildOutgoingLinks());
    infoBoxen.put("Attachments", buildAttachmentsBox());
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
      wikiContext.addSimpleValidationError("No pageId to restore");
      return null;
    }
    GWikiElement rei = wikiContext.getWikiWeb().findElement(restoreId);
    if (rei == null) {
      wikiContext.addSimpleValidationError("pageId cannot be found: " + restoreId);
      return null;
    }
    wikiContext.getWikiWeb().restoreWikiPage(wikiContext, rei);

    return getWikiContext().getWikiWeb().findElement(pageId);
  }

  public Object onCompare()
  {
    if (compareVersions == null) {
      initialize();
      wikiContext.addSimpleValidationError("no version selected to compare");
      return null;
    }
    if (compareVersions.length != 2) {
      initialize();
      wikiContext.addSimpleValidationError("select two versions to compare");
      return null;
    }
    String rd = wikiContext.localUrl("/edit/ComparePages")
        + "?leftPageId="
        + WebUtils.encodeUrlParam(compareVersions[0])
        + "&rightPageId="
        + WebUtils.encodeUrlParam(compareVersions[1])
        + "&backUrl="
        + WebUtils.encodeUrlParam(wikiContext.localUrl("/edit/PageInfo&pageId=") + this.pageId);
    return rd;
    // return null;
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
}

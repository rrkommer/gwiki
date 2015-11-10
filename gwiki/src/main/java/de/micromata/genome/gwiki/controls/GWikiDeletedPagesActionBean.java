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

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.a;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.th;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.util.matcher.EveryMatcher;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.string.ContainsIgnoreCaseMatcher;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * ActionBean provides information about all deleted pages.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiDeletedPagesActionBean extends ActionBeanBase
{
  private boolean list;

  private List<String> deletedPages = Collections.emptyList();

  private String listFilter;

  /**
   * May null if list.
   */
  private String pageId;

  private String versionBox;

  public Object onInit()
  {
    if (StringUtils.isEmpty(pageId) == true) {
      list = true;
    }
    return null;
  }

  public Object onFilter()
  {
    list = true;
    initListDeleted();
    return null;
  }

  public Object onViewItem()
  {
    if (StringUtils.isEmpty(pageId) == true) {
      wikiContext.addValidationError("gwiki.page.edit.DeletedPages.message.pageidnotset");
      list = true;
      return null;
    }
    List<GWikiElementInfo> versionsInfos = wikiContext.getWikiWeb().getStorage().getVersions(pageId);
    Collections.sort(versionsInfos, new Comparator<GWikiElementInfo>() {

      public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
      {
        return o2.getId().compareTo(o1.getId());
      }
    });

    XmlElement ta = GWikiPageInfoActionBean.getStandardTable();
    ta.nest(//
    tr(//
    th(text(translate("gwiki.page.edit.DeletedPages.col.author"))), //
        th(text(translate("gwiki.page.edit.DeletedPages.col.time"))), //
        th(text(translate("gwiki.page.edit.DeletedPages.col.action"))) //
    )//
    );
    for (GWikiElementInfo ei : versionsInfos) {

      ta.nest(//
      tr(//
      td(text(StringUtils.defaultString(ei.getProps().getStringValue(GWikiPropKeys.MODIFIEDBY), "Unknown"))), //
          td(text(getDisplayDate(ei.getProps().getDateValue(GWikiPropKeys.MODIFIEDAT)))), //
          td(//
          a(attrs("href", wikiContext.localUrl(ei.getId())), text(translate("gwiki.page.edit.DeletedPages.link.view"))), //
              a(attrs("href", wikiContext.localUrl("edit/PageInfo")
                  + "?restoreId="
                  + ei.getId()
                  + "&pageId="
                  + pageId
                  + "&method_onRestore=true"), //
                  text(translate("gwiki.page.edit.DeletedPages.link.restore"))//
              )) //
      ));
    }
    versionBox = GWikiPageInfoActionBean.getBoxFrame("Versionen", ta).toString();
    return null;
  }

  private String getDisplayDate(Date date)
  {
    if (date == null)
      return "unknown";
    return wikiContext.getUserDateString(date);
  }

  protected void initListDeleted()
  {
    Matcher<String> matcher = new EveryMatcher<String>();
    if (StringUtils.isNotBlank(listFilter) == true) {
      matcher = new ContainsIgnoreCaseMatcher<String>(listFilter);
    }
    deletedPages = wikiContext.getWikiWeb().getStorage().findDeletedPages(matcher);
    if (deletedPages == null || deletedPages.isEmpty() == true) {
      wikiContext.addSimpleValidationError("No deleted elements found");
    }
  }

  public boolean isList()
  {
    return list;
  }

  public void setList(boolean list)
  {
    this.list = list;
  }

  public List<String> getDeletedPages()
  {
    return deletedPages;
  }

  public void setDeletedPages(List<String> deletedPages)
  {
    this.deletedPages = deletedPages;
  }

  public String getListFilter()
  {
    return listFilter;
  }

  public void setListFilter(String listFilter)
  {
    this.listFilter = listFilter;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getVersionBox()
  {
    return versionBox;
  }

  public void setVersionBox(String versionBox)
  {
    this.versionBox = versionBox;
  }
}

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * @author Christian Claus (c.claus@micromata.de)
 * 
 */
public class GWikiTreeChildrenActionBean extends ActionBeanBase
{
  private String rootPage;

  private Map<String, String> rootCategories;

  public Object onLoadAsync()
  {
    GWikiElement el = null;
    List<GWikiElementInfo> childs = null;

    final String superCategory = wikiContext.getRequest().getParameter("id");
    final String urlField = wikiContext.getRequest().getParameter("urlField");
    final String titleField = wikiContext.getRequest().getParameter("titleField");
    final String openTarget = wikiContext.getRequest().getParameter("target");

    if (StringUtils.isBlank(superCategory)) {
      el = wikiContext.getWikiWeb().findElement(getRootPage());
      childs = wikiContext.getElementFinder().getAllDirectChildsByType(el.getElementInfo(), "gwiki");

      for (final GWikiElementInfo c : childs) {
        getRootCategories().put(c.getId(), c.getTitle());
      }

    } else {
      el = wikiContext.getWikiWeb().findElement(superCategory);
      childs = wikiContext.getElementFinder().getAllDirectChilds(el.getElementInfo());
    }

    final StringBuffer sb = new StringBuffer("");

    for (final GWikiElementInfo ei : childs) {
      if (wikiContext.getWikiWeb().getAuthorization().isAllowToView(wikiContext, ei) == false) {
        continue;
      }

      if (wikiContext.getElementFinder().getAllDirectChilds(ei).size() > 0) {
        sb.append("<li class='jstree-closed' ");
      } else {
        sb.append("<li ");
      }

      sb.append("id='").append(ei.getId()).append("'>");
      sb.append("<a onclick=\"");

      if (StringUtils.isEmpty(openTarget)) {
        if (StringUtils.isNotEmpty(urlField)) {
          sb.append("$('#" + urlField + "').val('" + ei.getId() + "');");
        }

        if (StringUtils.isNotEmpty(titleField)) {
          sb.append("$('#" + titleField + "').val('" + ei.getTitle() + "');");
        }
      } else if (StringUtils.equals(openTarget, "true")) {
        String targetLink = wikiContext.localUrl(ei.getId());
        sb.append("javascript:window.location.href='").append(targetLink).append("'");
      }

      sb.append("\" style=\"cursor:pointer\">");
      sb.append(ei.getTitle());
      sb.append("</a>");
      sb.append("</li>");
    }

    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

  /**
   * @param rootPage the rootPage to set
   */
  public void setRootPage(String rootPage)
  {
    this.rootPage = rootPage;
  }

  /**
   * @return the rootPage
   */
  public String getRootPage()
  {
    if (StringUtils.isBlank(rootPage)) {
      GWikiElement home = wikiContext.getWikiWeb().getHomeElement(wikiContext);
      if (home != null) {
        rootPage = home.getElementInfo().getId();
      }
    }
    return rootPage;
  }

  /**
   * @return the rootCategories
   */
  public Map<String, String> getRootCategories()
  {
    if (this.rootCategories == null) {
      this.rootCategories = new HashMap<String, String>();
    }
    return rootCategories;
  }
}

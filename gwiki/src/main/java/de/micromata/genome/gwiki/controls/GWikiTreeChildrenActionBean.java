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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import de.micromata.genome.gwiki.controls.GWikiWeditServiceActionBean.SearchType;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.matcher.GWikiElementPropMatcher;
import de.micromata.genome.gwiki.utils.JsonBuilder;
import de.micromata.genome.util.matcher.EqualsMatcher;

/**
 * @author Christian Claus (c.claus@micromata.de)
 * 
 */
public class GWikiTreeChildrenActionBean extends ActionBeanAjaxBase
{
  private String rootPage;

  private Map<String, String> rootCategories;

  public Object onLoadAsync()
  {
    GWikiElement el = null;
    List<GWikiElementInfo> rootElements = null;

    String superCategory = wikiContext.getRequest().getParameter("id");
    String urlField = wikiContext.getRequest().getParameter("urlField");
    String titleField = wikiContext.getRequest().getParameter("titleField");
    String openTarget = wikiContext.getRequest().getParameter("target");
    String type = wikiContext.getRequest().getParameter("type");
    if (StringUtils.isBlank(superCategory) || superCategory.equals("#") == true) {
      rootElements = getRootElements();
    } else {
      el = wikiContext.getWikiWeb().findElement(superCategory);
      rootElements = wikiContext.getElementFinder().getAllDirectChilds(el.getElementInfo());
    }
    SearchType searchType = SearchType.fromString(type);
    StringBuffer sb = new StringBuffer("");
    JsonArray rootNodes = JsonBuilder.array();

    // TODO expand to current id.
    for (GWikiElementInfo ei : rootElements) {
      JsonObject children = buildNodeInfo(ei, searchType);
      if (children != null) {
        rootNodes.add(children);
      }

    }

    return sendResponse(rootNodes);
  }

  private JsonObject buildNodeInfo(GWikiElementInfo ei, SearchType searchType)
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToView(wikiContext, ei) == false) {
      return null;
    }
    JsonObject ret = new JsonObject();
    String title = ei.getTitle();
    if (ei.getTitle().startsWith("I{") == true) {
      title = wikiContext.getTranslatedProp(title);
    }
    if (StringUtils.isBlank(title) == true) {
      return null;
    }
    List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChilds(ei);
    JsonArray childNodes = JsonBuilder.array();
    for (GWikiElementInfo sid : childs) {
      JsonObject subnode = buildNodeInfo(sid, searchType);
      if (subnode != null) {
        childNodes.add(subnode);
      }
    }
    SearchType st = SearchType.fromElementInfo(wikiContext, ei);
    boolean match = searchType.matches(st);
    if (match == false && childNodes.size() == 0) {
      return null;
    }
    ret.add("children", childNodes);
    JsonObject data = new JsonObject();
    ret.add("data", data);
    // TODO debug only
    //    if (childNodes.isEmpty() == false) {
    //      ret.add("state", JsonBuilder.map("opened", "true"));
    //    }
    data.add("url", ei.getId());
    ret.add("id", StringUtils.replace(ei.getId(), "/", "_"));
    //    ret.add("id", ei.getId());
    String targetLink = wikiContext.localUrl(ei.getId());

    //    data.add("url", targetLink);

    data.add("type", st.getElmentJsonType());

    data.add("matchtype", match);

    data.add("title", title);
    ret.add("text", title);
    return ret;
  }

  private List<GWikiElementInfo> getRootElements()
  {
    GWikiElementPropMatcher rootPageMatcher = new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE,
        new EqualsMatcher<String>(
            null));
    List<GWikiElement> rootPages = wikiContext.getElementFinder().getPages(rootPageMatcher);
    List<GWikiElementInfo> validRootPages = new ArrayList<GWikiElementInfo>();

    for (GWikiElement elem : rootPages) {
      if (elem.getElementInfo().isIndexed()
          && elem.getElementInfo().isViewable()
          && StringUtils.equals("gwiki", elem.getElementInfo().getType())
          && elem.getElementInfo().isNoToc() == false) {

        validRootPages.add(elem.getElementInfo());
      }
    }

    return validRootPages;
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
      rootPage = wikiContext.getWikiWeb().getWelcomePageId(wikiContext);
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

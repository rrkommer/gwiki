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
package de.micromata.genome.gwiki.pagetemplates_1_0.wizard;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * Wizard step for choosing category
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class CategoryStepWizardAction extends AbstractStepWizardAction
{
  /*
   * page that is the parent of categories to display
   */
  private String rootPage;

  /*
   * Category
   */
  private static final String NOT_A_CATEGORY = "-1";

  private Map<String, String> rootCategories;

  private String catSelectedCategory1;

  private String catSelectedCategory2;

  private String catSelectedCategory3;

  private String catSelectedCategory4;

  private String catSelectedCategory5;

  private String catPageTitle;

  private String catPageId;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase#onInit()
   */
  @Override
  public Object onInit()
  {
    fillCategories();
    return null;
  }

  public Object onSave()
  {
    if (getElement() == null) {
      return noForward();
    }

    final String parentPageId = getParentPageId();
    final String newPageId = getPageId();
    getElement().getElementInfo().setId(newPageId);
    getElement().getElementInfo().getProps().setStringValue(GWikiPropKeys.PARENTPAGE, parentPageId);
    getElement().getElementInfo().getProps().setStringValue(GWikiPropKeys.TITLE, catPageTitle);
    getElement().getElementInfo().getProps().setStringValue(GWikiPropKeys.AUTH_EDIT, "");
    return noForward();
  }

  public Object onValidate()
  {
    if (StringUtils.isBlank(catPageTitle) == true) {
      wikiContext.addValidationError("gwiki.page.articleWizard.error.noPageTitle");
    }
    if (StringUtils.isBlank(catPageId) == true) {
      wikiContext.addValidationError("gwiki.page.articleWizard.error.noPageId");
      return null;
    }

    // TODO stefan alle Branches durchsuchen
    if (wikiContext.getWikiWeb().findElement(getPageId()) != null) {
      wikiContext.addValidationError("gwiki.page.articleWizard.error.duplicatePageId");
    }
    try {
      String encodedPageId = URLEncoder.encode(catPageId, "UTF-8");
      if (catPageId.equalsIgnoreCase(encodedPageId) == false && catPageId.length() > 30) {
        wikiContext.addValidationError("gwiki.page.articleWizard.error.invalidPageId");
      }
    } catch (UnsupportedEncodingException ex) {
      GWikiLog.error("encoding error");
    }
    return null;
  }

  public boolean onIsVisible()
  {
    return true;
  }

  /**
   * Ajax-Handler for loading sub-categories asynchronously
   */
  public Object onLoadCategoryAsync()
  {
    final String sDepth = wikiContext.getRequest().getParameter("depth");
    final String superCategory = wikiContext.getRequest().getParameter("cat");
    if (StringUtils.isBlank(superCategory) || StringUtils.isBlank(sDepth)) {
      return noForward();
    }
    if (superCategory.equalsIgnoreCase(NOT_A_CATEGORY)) {
      return noForward();
    }

    final int depth = Integer.parseInt(sDepth);
    final String defaultText = wikiContext.getWikiWeb().getI18nProvider()
        .translate(wikiContext, "gwiki.page.articleWizard.category.choose");

    final GWikiElement el = wikiContext.getWikiWeb().findElement(superCategory);
    final List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChilds(el.getElementInfo());

    final StringBuffer sb = new StringBuffer("<select name=\"catSelectedCategory").append(depth).append("\" onchange=\"loadAsync(")
        .append(depth + 1).append(")\">");
    sb.append("<option value=\"").append(NOT_A_CATEGORY).append("\">").append(defaultText).append("</option>");
    for (final GWikiElementInfo ei : childs) {
      sb.append("<option value=\"").append(ei.getId()).append("\">").append(ei.getTitle()).append("</option>");
    }
    sb.append("</select>");

    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

  // ///////////////////////////////////
  // Helper
  // ///////////////////////////////////
  /**
   * @return the page id of super category of new page
   */
  private String getParentPageId()
  {
    if (StringUtils.isNotBlank(this.catSelectedCategory5) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.catSelectedCategory5) == false) {
      return this.catSelectedCategory5;
    }
    if (StringUtils.isNotBlank(this.catSelectedCategory4) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.catSelectedCategory4) == false) {
      return this.catSelectedCategory4;
    }
    if (StringUtils.isNotBlank(this.catSelectedCategory3) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.catSelectedCategory3) == false) {
      return this.catSelectedCategory3;
    }
    if (StringUtils.isNotBlank(this.catSelectedCategory2) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.catSelectedCategory2) == false) {
      return this.catSelectedCategory2;
    }
    if (StringUtils.isNotBlank(this.catSelectedCategory1) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.catSelectedCategory1) == false) {
      return this.catSelectedCategory1;
    }
    return getRootPage();
  }

  /**
   * @return
   */
  private String getPageId()
  {
    return getParentPageId() + "/" + WebUtils.encodeUrlParam(catPageId);
  }

  /**
   * Fills the root categories and recursively all subcategories
   */
  private void fillCategories()
  {
    final GWikiElement el = wikiContext.getWikiWeb().findElement(getRootPage());
    final List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChildsByType(el.getElementInfo(), "gwiki");

    for (final GWikiElementInfo c : childs) {
      getRootCategories().put(c.getId(), c.getTitle());
    }
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
   * @param rootPage the rootPage to set
   */
  public void setRootPage(String rootPage)
  {
    this.rootPage = rootPage;
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

  /**
   * @return the pageTitle
   */
  public String getCatPageTitle()
  {
    return catPageTitle;
  }

  /**
   * @param pageTitle the pageTitle to set
   */
  public void setCatPageTitle(String pageTitle)
  {
    this.catPageTitle = pageTitle;
  }

  /**
   * @param selectedCategory1 the selectedCategory1 to set
   */
  public void setCatSelectedCategory1(String selectedCategory1)
  {
    this.catSelectedCategory1 = selectedCategory1;
  }

  /**
   * @return the selectedCategory1
   */
  public String getCatSelectedCategory1()
  {
    return catSelectedCategory1;
  }

  /**
   * @param selectedCategory2 the selectedCategory2 to set
   */
  public void setCatSelectedCategory2(String selectedCategory2)
  {
    this.catSelectedCategory2 = selectedCategory2;
  }

  /**
   * @return the selectedCategory2
   */
  public String getCatSelectedCategory2()
  {
    return catSelectedCategory2;
  }

  /**
   * @param selectedCategory3 the selectedCategory3 to set
   */
  public void setCatSelectedCategory3(String selectedCategory3)
  {
    this.catSelectedCategory3 = selectedCategory3;
  }

  /**
   * @return the selectedCategory3
   */
  public String getCatSelectedCategory3()
  {
    return catSelectedCategory3;
  }

  /**
   * @param selectedCategory4 the selectedCategory4 to set
   */
  public void setCatSelectedCategory4(String selectedCategory4)
  {
    this.catSelectedCategory4 = selectedCategory4;
  }

  /**
   * @return the selectedCategory4
   */
  public String getCatSelectedCategory4()
  {
    return catSelectedCategory4;
  }

  /**
   * @param selectedCategory5 the selectedCategory5 to set
   */
  public void setCatSelectedCategory5(String selectedCategory5)
  {
    this.catSelectedCategory5 = selectedCategory5;
  }

  /**
   * @return the selectedCategory5
   */
  public String getCatSelectedCategory5()
  {
    return catSelectedCategory5;
  }

  /**
   * @param pageId the pageId to set
   */
  public void setCatPageId(String pageId)
  {
    this.catPageId = pageId;
  }

  /**
   * @return the pageId
   */
  public String getCatPageId()
  {
    return catPageId;
  }
}
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
package de.micromata.genome.gwiki.pagetemplates_1_0.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * Wizard step for choosing category
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class CategoryStepWizardAction extends ActionBeanBase
{

  private GWikiElement element;

  /*
   * page that is the parent of categories to display
   */
  private String rootPage;

  /*
   * Category
   */
  private static final String NOT_A_CATEGORY = "-1";

  private Map<String, String> rootCategories;

  private String selectedCategory1;

  private String selectedCategory2;

  private String selectedCategory3;

  private String selectedCategory4;

  private String selectedCategory5;

  private String pageTitle;

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
    if (this.element == null) {
      return noForward();
    }

    final String parentPage = getParentPageId();
    
    final String newPageId = parentPage + "/" + pageTitle;
    element.getElementInfo().setId(newPageId);
    element.getElementInfo().getProps().setStringValue(GWikiPropKeys.PARENTPAGE, parentPage);
    element.getElementInfo().getProps().setStringValue(GWikiPropKeys.TITLE, pageTitle);

    return noForward();
  }

  public Object onValidate()
  {
    if (StringUtils.isBlank(this.pageTitle) == true) {
      wikiContext.addValidationError("gwiki.page.articleWizard.error.noPageTitle");
    }
    return null;
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

    final StringBuffer sb = new StringBuffer("<select name=\"selectedCategory").append(depth).append("\" onchange=\"loadAsync(")
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
    if (StringUtils.isNotBlank(this.selectedCategory5) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory5) == false) {
      return this.selectedCategory5;
    }
    if (StringUtils.isNotBlank(this.selectedCategory4) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory4) == false) {
      return this.selectedCategory4;
    }
    if (StringUtils.isNotBlank(this.selectedCategory3) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory3) == false) {
      return this.selectedCategory3;
    }
    if (StringUtils.isNotBlank(this.selectedCategory2) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory2) == false) {
      return this.selectedCategory2;
    }
    if (StringUtils.isNotBlank(this.selectedCategory1) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory1) == false) {
      return this.selectedCategory1;
    }
    return getRootPage();
  }

  /**
   * Fills the root categories and recursively all subcategories
   */
  private void fillCategories()
  {
    final GWikiElement el = wikiContext.getWikiWeb().findElement(getRootPage());
    final List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChilds(el.getElementInfo());

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
  public String getPageTitle()
  {
    return pageTitle;
  }

  /**
   * @param pageTitle the pageTitle to set
   */
  public void setPageTitle(String pageTitle)
  {
    this.pageTitle = pageTitle;
  }

  /**
   * @param selectedCategory1 the selectedCategory1 to set
   */
  public void setSelectedCategory1(String selectedCategory1)
  {
    this.selectedCategory1 = selectedCategory1;
  }

  /**
   * @return the selectedCategory1
   */
  public String getSelectedCategory1()
  {
    return selectedCategory1;
  }

  /**
   * @param selectedCategory2 the selectedCategory2 to set
   */
  public void setSelectedCategory2(String selectedCategory2)
  {
    this.selectedCategory2 = selectedCategory2;
  }

  /**
   * @return the selectedCategory2
   */
  public String getSelectedCategory2()
  {
    return selectedCategory2;
  }

  /**
   * @param selectedCategory3 the selectedCategory3 to set
   */
  public void setSelectedCategory3(String selectedCategory3)
  {
    this.selectedCategory3 = selectedCategory3;
  }

  /**
   * @return the selectedCategory3
   */
  public String getSelectedCategory3()
  {
    return selectedCategory3;
  }

  /**
   * @param selectedCategory4 the selectedCategory4 to set
   */
  public void setSelectedCategory4(String selectedCategory4)
  {
    this.selectedCategory4 = selectedCategory4;
  }

  /**
   * @return the selectedCategory4
   */
  public String getSelectedCategory4()
  {
    return selectedCategory4;
  }

  /**
   * @param selectedCategory5 the selectedCategory5 to set
   */
  public void setSelectedCategory5(String selectedCategory5)
  {
    this.selectedCategory5 = selectedCategory5;
  }

  /**
   * @return the selectedCategory5
   */
  public String getSelectedCategory5()
  {
    return selectedCategory5;
  }

  /**
   * @param element the element to set
   */
  public void setElement(GWikiElement element)
  {
    this.element = element;
  }

  /**
   * @return the element
   */
  public GWikiElement getElement()
  {
    return element;
  }
}

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

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class CategoryStepWizardAction extends ActionBeanBase
{

  private static final String DEFAULT_ROOT_PAGE = "volkswagen/Index";

  private String rootPage = DEFAULT_ROOT_PAGE;

  /*
   * Category
   */
  private static final String NOT_A_CATEGORY = "-1";

  private static final String CATEGORY_SUFFIX = "/Index";

  private Map<String, String> rootCategories;

  private Map<String, String> allCategories;

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
    // save new empty page
    final String newPageId = getNewPageId();
    final GWikiElement newPage = GWikiWebUtils.createNewElement(wikiContext, newPageId, "admin/templates/StandardWikiPageMetaTemplate",
        this.pageTitle);
    newPage.getElementInfo().getProps().setStringValue(GWikiPropKeys.PARENTPAGE, getParentPageId());

    // saves element in draft branch
    wikiContext.getWikiWeb().saveElement(wikiContext, newPage, false);

    // save page-id in request-attribute for possible later usage
    wikiContext.setRequestAttribute(PAGE_ID_REQ_ATTR, newPageId);

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
   * Creates a new category (Index-Page)
   */
  public Object onCreateCategory()
  {
    final String newCategoryName = wikiContext.getRequest().getParameter("categoryName");
    final String newCategoryDesc = wikiContext.getRequest().getParameter("categoryDesc");
    final String newCategoryToc = wikiContext.getRequest().getParameter("categoryToc");
    String parentCategory = wikiContext.getRequest().getParameter("parentCategory");

    final boolean toc = "true".equalsIgnoreCase(newCategoryToc);

    if (StringUtils.isBlank(newCategoryName) == true) {
      wikiContext.addValidationFieldError("gwiki.page.articleWizard.error.noCategoryName", "newCategoryName");
      return null;
    }

    String newCategoryId;
    if (NOT_A_CATEGORY.equalsIgnoreCase(parentCategory) == true) {
      newCategoryId = StringUtils.removeEnd(rootPage, CATEGORY_SUFFIX) + "/" + newCategoryName + CATEGORY_SUFFIX;
      parentCategory = rootPage;
    } else {
      newCategoryId = StringUtils.removeEnd(parentCategory, CATEGORY_SUFFIX) + "/" + newCategoryName + CATEGORY_SUFFIX;
    }

    final GWikiElement newCategoryElement = GWikiWebUtils.createNewElement(wikiContext, newCategoryId,
        "admin/templates/StandardWikiPageMetaTemplate", newCategoryName);
    final GWikiArtefakt< ? > artefakt = newCategoryElement.getPart("MainPage");
    if (artefakt instanceof GWikiWikiPageArtefakt == false) {
      return null;
    }

    final GWikiProps props = newCategoryElement.getElementInfo().getProps();
    props.setStringValue(GWikiPropKeys.PARENTPAGE, parentCategory);

    // save new category in draft
    final GWikiWikiPageArtefakt pageArtfekt = (GWikiWikiPageArtefakt) artefakt;
    final StringBuilder sb = new StringBuilder();
    if (StringUtils.isNotBlank(newCategoryDesc) == true) {
      sb.append("{pageintro}").append(newCategoryDesc).append("{pageintro}").append("\n");
    }
    if (toc == true) {
      sb.append("{children:depth=2|sort=title|withPageIntro=true}").append("\n");
    }
    pageArtfekt.setStorageData(sb.toString());
    wikiContext.getWikiWeb().saveElement(wikiContext, newCategoryElement, false);

    // reload categories
    fillCategories();
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
      if (ei.getId().endsWith(CATEGORY_SUFFIX) == false) {
        continue;
      }
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
      return this.selectedCategory5 + CATEGORY_SUFFIX;
    }
    if (StringUtils.isNotBlank(this.selectedCategory4) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory4) == false) {
      return this.selectedCategory4 + CATEGORY_SUFFIX;
    }
    if (StringUtils.isNotBlank(this.selectedCategory3) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory3) == false) {
      return this.selectedCategory3 + CATEGORY_SUFFIX;
    }
    if (StringUtils.isNotBlank(this.selectedCategory2) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory2) == false) {
      return this.selectedCategory2 + CATEGORY_SUFFIX;
    }
    return this.selectedCategory1 + CATEGORY_SUFFIX;
  }

  /**
   * TODO stefan: Muss page-id alle Kategorien enthalten oder genügt die korrekte parentverlinkung
   * 
   * @return page id of new page
   */
  private String getNewPageId()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(this.selectedCategory1).append("/");

    // optional subcategories
    if (StringUtils.isNotBlank(this.selectedCategory2) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory2) == false) {
      sb.append(StringUtils.substringAfterLast(this.selectedCategory2, "/")).append("/");
    }
    if (StringUtils.isNotBlank(this.selectedCategory3) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory3) == false) {
      sb.append(StringUtils.substringAfterLast(this.selectedCategory3, "/")).append("/");
    }
    if (StringUtils.isNotBlank(this.selectedCategory4) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory4) == false) {
      sb.append(StringUtils.substringAfterLast(this.selectedCategory4, "/")).append("/");
    }
    if (StringUtils.isNotBlank(this.selectedCategory5) == true && NOT_A_CATEGORY.equalsIgnoreCase(this.selectedCategory5) == false) {
      sb.append(StringUtils.substringAfterLast(this.selectedCategory5, "/")).append("/");
    }
    sb.append(this.pageTitle);
    return sb.toString();
  }

  /**
   * Fills the root categories and recursively all subcategories
   */
  private void fillCategories()
  {
    final GWikiElement el = wikiContext.getWikiWeb().findElement(rootPage);
    final List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChilds(el.getElementInfo());

    for (final GWikiElementInfo c : childs) {
      // all categories ends on "/Index"
      if (c.getId().endsWith(CATEGORY_SUFFIX) == false) {
        continue;
      }
      getRootCategories().put(c.getId(), c.getTitle());
      getAllCategories().put(c.getId(), c.getTitle());

      // fill recursively all categories
      fillAllCategories(c);
    }
  }

  /**
   * @param c
   */
  private void fillAllCategories(final GWikiElementInfo parentEi)
  {
    final List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChilds(parentEi);
    for (final GWikiElementInfo c : childs) {
      if (c.getId().endsWith(CATEGORY_SUFFIX) == false) {
        continue;
      }
      getAllCategories().put(c.getId(), c.getTitle());
      fillAllCategories(c);
    }
  }

  /**
   * @return the rootPage
   */
  public String getRootPage()
  {
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
   * @return the allCategories
   */
  public Map<String, String> getAllCategories()
  {
    if (this.allCategories == null) {
      this.allCategories = new HashMap<String, String>();
    }
    return this.allCategories;
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
    if (selectedCategory1.endsWith(CATEGORY_SUFFIX)) {
      selectedCategory1 = StringUtils.removeEnd(selectedCategory1, CATEGORY_SUFFIX);
    }
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
    if (selectedCategory2.endsWith(CATEGORY_SUFFIX)) {
      selectedCategory2 = StringUtils.removeEnd(selectedCategory2, CATEGORY_SUFFIX);
    }
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
    if (selectedCategory3.endsWith(CATEGORY_SUFFIX)) {
      selectedCategory3 = StringUtils.removeEnd(selectedCategory3, CATEGORY_SUFFIX);
    }
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
    if (selectedCategory4.endsWith(CATEGORY_SUFFIX)) {
      selectedCategory4 = StringUtils.removeEnd(selectedCategory4, CATEGORY_SUFFIX);
    }
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
    if (selectedCategory5.endsWith(CATEGORY_SUFFIX)) {
      selectedCategory5 = StringUtils.removeEnd(selectedCategory5, CATEGORY_SUFFIX);
    }
    this.selectedCategory5 = selectedCategory5;
  }

  /**
   * @return the selectedCategory5
   */
  public String getSelectedCategory5()
  {
    return selectedCategory5;
  }
}

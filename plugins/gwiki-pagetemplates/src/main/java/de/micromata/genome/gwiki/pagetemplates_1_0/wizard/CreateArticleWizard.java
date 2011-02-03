package de.micromata.genome.gwiki.pagetemplates_1_0.wizard;

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.a;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.input;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.table;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.th;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAttachment;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * Wizard for creating articles
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class CreateArticleWizard extends ActionBeanBase
{

  private static final String DEFAULT_ROOT_PAGE = "volkswagen/Index";

  private String rootPage = DEFAULT_ROOT_PAGE;

  private static final String DRAFT_ID = "_DRAFT";

  private String selectedCategory1;

  private String selectedCategory2;

  private String selectedCategory3;

  private String selectedCategory4;

  private String selectedCategory5;

  private String selectedPageTemplate;

  private String pageTitle;

  private Map<String, String> rootCategories;

  private Map<String, String> allCategories;

  private String newCategoryName;

  private String newCategoryDesc;

  private boolean newCategoryToc;

  private String parentCategory;

  private static final String NOT_A_CATEGORY = "-1";

  private static final String CATEGORY_SUFFIX = "/Index";

  @Override
  public Object onInit()
  {
    fillCategories();
    return null;
  }

  /**
   * renders a table with available page templates
   */
  public void renderTemplates()
  {
    // find page templates
    final Matcher<String> m = new BooleanListRulesFactory<String>().createMatcher("*tpl/*Template*");
    final List<GWikiElementInfo> ret = wikiContext.getElementFinder().getPageInfos(new GWikiPageIdMatcher(wikiContext, m));

    // table header
    XmlElement table = table(attrs("border", "1", "cellspacing", "0", "cellpadding", "2", "width", "100%"));
    table.nest(//
        tr(//
            th(text(translate("gwiki.page.articleWizard.template.select"))), //
            th(text(translate("gwiki.page.articleWizard.template.name"))), //
            th(text(translate("gwiki.page.articleWizard.template.desc"))), //
            th(text(translate("gwiki.page.articleWizard.template.preview"))) //
        ));

    // render each template in row
    for (final GWikiElementInfo ei : ret) {
      final GWikiElement el = wikiContext.getWikiWeb().findElement(ei.getId());
      if (el == null || ei.isViewable() == false) {
        continue;
      }

      final String part = "MainPage";
      GWikiArtefakt< ? > art = null;
      if (StringUtils.isNotEmpty(part) == true) {
        art = el.getPart(part);
      } else {
        art = el.getMainPart();
      }
      if ((art instanceof GWikiExecutableArtefakt< ? >) == false || art instanceof GWikiAttachment) {
        continue;
      }
      final String desc = StringUtils.defaultIfEmpty(ei.getProps().getStringValue("DESCRIPTION"), "n/a");
      final String name = StringUtils.defaultIfEmpty(ei.getProps().getStringValue("NAME"), "n/a");
      final GWikiExecutableArtefakt< ? > exec = (GWikiExecutableArtefakt< ? >) art;
      final String renderedPreview = renderPreview(exec);

      StringBuffer sb = new StringBuffer();
      sb.append("displayHilfeLayer('<div style=\"font-size:0.6em;size:0.6em;\">");
      sb.append(StringEscapeUtils.escapeJavaScript(renderedPreview));
      sb.append("</div>");
      sb.append("', '").append(wikiContext.genHtmlId(""));
      sb.append("')");

      // render template row
      table.nest( //
          tr(//
              td(input("type", "radio", "name", "selectedPageTemplate", "value", ei.getId())), //
              td(text(name)), //
              td(text(desc)), //
              td(new String[][]{{"valign", "top"}}, //
                a(new String[][]{ //
                    {"class", "wikiSmartTag"},{"href", "#"},{"onclick" , "return false;"},
                    {"onmouseout", "doNotOpenHilfeLayer();"}, {"onmouseover", sb.toString()}//
                },                    
                text(translate("gwiki.page.articleWizard.template.preview")))
              )
          ));
    }
    
    // write table
    wikiContext.append(table.toString());
  }

  /**
   * Creates a new category (Index-Page)
   */
  public Object onCreateCategory()
  {
    if (StringUtils.isBlank(this.newCategoryName) == true) {
      wikiContext.addValidationFieldError("gwiki.page.articleWizard.error.noCategoryName", "newCategoryName");
      return null;
    }

    String newCategoryId;
    if (NOT_A_CATEGORY.equalsIgnoreCase(this.parentCategory) == true) {
      newCategoryId = StringUtils.removeEnd(rootPage, CATEGORY_SUFFIX) + "/" + newCategoryName + CATEGORY_SUFFIX;
      this.parentCategory = rootPage;
    } else {
      newCategoryId = StringUtils.removeEnd(this.parentCategory, CATEGORY_SUFFIX) + "/" + newCategoryName + CATEGORY_SUFFIX;
    }

    final GWikiElement newCategoryElement = GWikiWebUtils.createNewElement(wikiContext, newCategoryId,
        "admin/templates/StandardWikiPageMetaTemplate", this.newCategoryName);
    final GWikiArtefakt< ? > artefakt = newCategoryElement.getPart("MainPage");
    if (artefakt instanceof GWikiWikiPageArtefakt == false) {
      return null;
    }

    final GWikiProps props = newCategoryElement.getElementInfo().getProps();
    props.setStringValue(GWikiPropKeys.PARENTPAGE, this.parentCategory);

    ensureDraftBranch(getWikiSelector());

    // save new category in draft
    wikiContext.runInTenantContext(DRAFT_ID, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        final GWikiWikiPageArtefakt pageArtfekt = (GWikiWikiPageArtefakt) artefakt;
        final StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(CreateArticleWizard.this.newCategoryDesc) == true) {
          sb.append("{pageintro}").append(CreateArticleWizard.this.newCategoryDesc).append("{pageintro}").append("\n");
        }

        if (CreateArticleWizard.this.newCategoryToc == true) {
          sb.append("{children:depth=2|sort=title|withPageIntro=true}").append("\n");
        }

        pageArtfekt.setStorageData(sb.toString());

        wikiContext.getWikiWeb().saveElement(wikiContext, newCategoryElement, false);
        return null;
      }
    });

    // reload categories
    fillCategories();
    setNewCategoryDesc(null);
    setNewCategoryName(null);
    setNewCategoryToc(false);

    return null;
  }

  /**
   * Ajax-Handler for loading sub-categories asynchronously
   * 
   * @return
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
    wikiContext.runInTenantContext(DRAFT_ID, getWikiSelector(), new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
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
        return null;
      }
    });

    return noForward();
  }

  /**
   * Handler for finish wizard and creating the article
   * 
   * @return redirect to new page
   */
  public Object onCreateArticle()
  {
    final boolean dataPresent = validateDataPresent();
    if (dataPresent == false) {
      fillCategories();
      return null;
    }

    final GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    if (wikiSelector == null) {
      fillCategories();
      return null;
    }

    // load page template
    final GWikiElement tpl = wikiContext.getWikiWeb().findElement(selectedPageTemplate);
    if (tpl == null) {
      fillCategories();
      wikiContext.addValidationError("gwiki.page.articleWizard.error.noTemplate");
      return false;
    }
    final GWikiElementInfo tplEi = tpl.getElementInfo();

    // save new page
    final String newPageId = getNewPageId();
    final GWikiElementInfo pageEi = new GWikiElementInfo(tplEi);
    pageEi.setId(newPageId);

    // switch template from to standard wikipage template
    final GWikiMetaTemplate wikiPageMetaTemplate = wikiContext.getWikiWeb()
        .findMetaTemplate("admin/templates/StandardWikiPageMetaTemplate");
    if (wikiPageMetaTemplate != null) {
      pageEi.setMetaTemplate(wikiPageMetaTemplate);
    }

    final GWikiProps props = pageEi.getProps();
    props.setStringValue(GWikiPropKeys.TITLE, this.pageTitle);
    props.setStringValue(GWikiPropKeys.PARENTPAGE, getParentPageId());
    props.setStringValue(GWikiPropKeys.WIKIMETATEMPLATE, pageEi.getMetaTemplate().getPageId());
    props.remove("DESCRIPTION");
    props.remove("NAME");
    tpl.setElementInfo(pageEi);

    // ensures that all required meta files are present
    ensureDraftBranch(wikiSelector);

    // saves element in draft branch
    wikiContext.runInTenantContext(DRAFT_ID, wikiSelector, new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        wikiContext.getWikiWeb().saveElement(wikiContext, tpl, false);
        return null;
      }
    });

    wikiSelector.enterTenant(wikiContext, DRAFT_ID);
    return newPageId;
  }

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
   * Ensures that all required branch meta files are present. if not they will be created
   */
  private void ensureDraftBranch(final GWikiMultipleWikiSelector wikiSelector)
  {
    wikiContext.runInTenantContext(DRAFT_ID, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        // ensure filestats present
        final GWikiElement fileStats = wikiContext.getWikiWeb().findElement("admin/branch/intern/BranchFileStats");
        if (fileStats == null) {
          final GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, "admin/branch/intern/BranchFileStats",
              "admin/templates/intern/GWikiBranchFileStatsTemplate", "Branch File Stats");
          wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
        }

        // ensure branchinfo present
        final GWikiElement infoElement = wikiContext.getWikiWeb().findElement("admin/branch/intern/BranchInfoElement");
        if (infoElement == null) {
          final GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, "admin/branch/intern/BranchInfoElement",
              "admin/templates/intern/GWikiBranchInfoElementTemplate", "BranchInfo");
          final GWikiArtefakt< ? > artefakt = el.getMainPart();

          final GWikiPropsArtefakt art = (GWikiPropsArtefakt) artefakt;
          final GWikiProps props = art.getCompiledObject();
          props.setStringValue("BRANCH_ID", DRAFT_ID);
          props.setStringValue("DESCRIPTION", "Draft branch");
          props.setStringValue("BRANCH_STATE", "OFFLINE");
          props.setStringValue("RELEASE_DATE", "");
          props.setStringValue("RELEASE_END_DATE", "");

          wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
        }
        return null;
      }
    });
  }

  /**
   * Generates HTML Output of given executable artefakt
   * 
   * @param exec artefakt
   * @return rendered output
   */
  private String renderPreview(final GWikiExecutableArtefakt< ? > exec)
  {
    final GWikiStandaloneContext standaloneContext = GWikiStandaloneContext.create();
    standaloneContext.setRequestAttribute(GWikiFragmentImage.WIKI_MAX_IMAGE_WIDTH, "100px");
    standaloneContext.setRenderMode(RenderModes.LocalImageLinks.getFlag());
    standaloneContext.setWikiElement(wikiContext.getCurrentElement());
    standaloneContext.setCurrentPart(exec);
    exec.render(standaloneContext);
    standaloneContext.flush();
    return standaloneContext.getOutString();
  }

  private GWikiMultipleWikiSelector getWikiSelector()
  {
    final GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null) {
      wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.tenantsNotSupported");
      return null;
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == true) {
      return (GWikiMultipleWikiSelector) wikiSelector;
    }
    return null;
  }

  /**
   * Fills the root categories and recursively all subcategories
   */
  private void fillCategories()
  {
    wikiContext.runInTenantContext(DRAFT_ID, getWikiSelector(), new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
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
        return null;
      }
    });
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
   * Validates required data
   */
  private boolean validateDataPresent()
  {
    if (StringUtils.isBlank(this.getSelectedCategory1()) == true) {
      wikiContext.addValidationError("gwiki.page.articleWizard.error.noCategory");
      return false;
    }
    if (StringUtils.isBlank(this.pageTitle) == true) {
      wikiContext.addValidationError("gwiki.page.articleWizard.error.noPageTitle");
      return false;
    }

    if (StringUtils.isBlank(this.selectedPageTemplate)) {
      wikiContext.addValidationError("gwiki.page.articleWizard.error.noTemplate");
      return false;
    }
    return true;
  }

  /**
   * @param selectedPageTemplate the selectedPageTemplate to set
   */
  public void setSelectedPageTemplate(final String selectedPageTemplate)
  {
    this.selectedPageTemplate = selectedPageTemplate;
  }

  /**
   * @return the selectedPageTemplate
   */
  public String getSelectedPageTemplate()
  {
    return selectedPageTemplate;
  }

  /**
   * @param pageTitle the pageTitle to set
   */
  public void setPageTitle(final String pageTitle)
  {
    this.pageTitle = pageTitle;
  }

  /**
   * @return the pageTitle
   */
  public String getPageTitle()
  {
    return pageTitle;
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

  /**
   * @param rootCategories the rootCategories to set
   */
  public void setRootCategories(final Map<String, String> rootCategories)
  {
    this.rootCategories = rootCategories;
  }

  /**
   * @return the rootCategories
   */
  public Map<String, String> getRootCategories()
  {
    if (this.rootCategories == null) {
      this.rootCategories = new HashMap<String, String>();
    }
    return this.rootCategories;
  }

  /**
   * @param allCategories the allCategories to set
   */
  public void setAllCategories(final Map<String, String> allCategories)
  {
    this.allCategories = allCategories;
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
   * @param parentCategory the parentCategory to set
   */
  public void setParentCategory(final String parentCategory)
  {
    this.parentCategory = parentCategory;
  }

  /**
   * @return the parentCategory
   */
  public String getParentCategory()
  {
    return parentCategory;
  }

  /**
   * @param newCategoryName the newCategoryName to set
   */
  public void setNewCategoryName(final String newCategoryName)
  {
    this.newCategoryName = newCategoryName;
  }

  /**
   * @return the newCategoryName
   */
  public String getNewCategoryName()
  {
    return newCategoryName;
  }

  /**
   * @param newCategoryDesc the newCategoryDesc to set
   */
  public void setNewCategoryDesc(final String newCategoryDesc)
  {
    this.newCategoryDesc = newCategoryDesc;
  }

  /**
   * @return the newCategoryDesc
   */
  public String getNewCategoryDesc()
  {
    return newCategoryDesc;
  }

  /**
   * @param newCategoryToc the newCategoryToc to set
   */
  public void setNewCategoryToc(final boolean newCategoryToc)
  {
    this.newCategoryToc = newCategoryToc;
  }

  /**
   * @return the newCategoryToc
   */
  public boolean isNewCategoryToc()
  {
    return newCategoryToc;
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
    return rootPage;
  }

}
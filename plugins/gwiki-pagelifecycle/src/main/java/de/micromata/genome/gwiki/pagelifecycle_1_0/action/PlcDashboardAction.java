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
package de.micromata.genome.gwiki.pagelifecycle_1_0.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.GWikiBranchFileStatsArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileInfoWrapper;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Dashboard for pagelifecycle specific actions
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class PlcDashboardAction extends PlcActionBeanBase
{
  private String orderBy = "RELEASE_DATE";

  /**
   * content to display
   */
  private List<FileInfoWrapper> content = new ArrayList<FileInfoWrapper>();

  /**
   * blacklist of files which not should be considered in content list
   */
  private Matcher<String> blackListMatcher = new BooleanListRulesFactory<String>().createMatcher("*intern/*,*admin/*");

  private String selectedPageId;

  private String selectedTenant;

  private List<String> actionLinks = Arrays.asList("edit/pagetemplates/PageWizard", "edit/pagelifecycle/dashboard/PlcCreateBranchPopup");

  /**
   * Filters
   */
  private String titleFilter;

  private String catFilter;

  private String authorFilter;

  private String statusFilter;

  private String dateFilter;

  private String releaseFilter;

  private String assigneeFilter;
  
  /**
   * Filter of file types
   */
  private String viewFilter = "gwiki";

  private int offset;

  @Override
  public Object onInit()
  {
    updateList();
    return null;
  }

  public Object onViewPageInTenantContext()
  {
    String pageId = getSelectedPageId();
    String tenant = getSelectedTenant();
    if (StringUtils.isBlank(pageId) == true || StringUtils.isBlank(tenant) == true) {
      return null;
    }
    getWikiSelector().enterTenant(wikiContext, tenant);
    return pageId;
  }

  /**
   * Loads the content of the several branches
   */
  private Object updateList()
  {
    GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    if (wikiSelector == null) {
      wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.loadtenantcontent");
      return null;
    }
    List<String> tenants = wikiSelector.getMptIdSelector().getTenants(GWikiWeb.getRootWiki());
    if (tenants == null || tenants.size() == 0) {
      return null;

    }

    final List<FileInfoWrapper> files = new ArrayList<FileInfoWrapper>();

    for (final String tenant : tenants) {
      wikiContext.runInTenantContext(tenant, wikiSelector, new CallableX<Void, RuntimeException>() {
        public Void call() throws RuntimeException
        {
          // get all elemtinfos of tenant
          List<GWikiElementInfo> tenantContent = wikiContext.getElementFinder().getPageInfos(new MatcherBase<GWikiElementInfo>() {
            private static final long serialVersionUID = -6020166500681050082L;

            public boolean match(GWikiElementInfo ei)
            {
              String tid = ei.getProps().getStringValue(GWikiPropKeys.TENANT_ID);
              String et = "";
              if (ei.getMetaTemplate() != null) {
                et = ei.getMetaTemplate().getElementType();
              }
              if (StringUtils.isNotBlank(getViewFilter()) == true) {
                return StringUtils.equals(tenant, tid) && StringUtils.equals(getViewFilter(), et);
              } else {
                return StringUtils.equals(tenant, tid);
              }
              
            }
          });

          // TODO stefan PlcUtils nehmen
          // if no branch filestats present add empty filestats for each item
          GWikiElement branchFileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
          if (branchFileStats == null || branchFileStats.getMainPart() == null) {
            for (GWikiElementInfo ei : tenantContent) {
              files.add(new FileInfoWrapper(tenant, ei, null));
            }
            return null;
          }

          GWikiArtefakt< ? > artefakt = branchFileStats.getMainPart();
          if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
            return null;
          }

          GWikiBranchFileStatsArtefakt branchArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
          BranchFileStats fileStats = branchArtefakt.getCompiledObject();
          // collecting filestats information for each item
          for (GWikiElementInfo ei : tenantContent) {
            // ignore blacklisted files
            if (blackListMatcher.match(ei.getId()) == true) {
              continue;
            }
            FileStatsDO fileStatsDO = fileStats.getFileStatsForId(ei.getId());
            if (fileStatsDO == null) {
              files.add(new FileInfoWrapper(tenant, ei, null));
            } else {
              files.add(new FileInfoWrapper(tenant, ei, fileStatsDO));
            }
          }
          return null;
        }
      });
    }

    List<FileInfoWrapper> filteredList = (List<FileInfoWrapper>) CollectionUtils.select(files, new FileInfoFilterPredicate());

    Collections.sort(filteredList, new FileInfoComparator(orderBy));
    getContent().addAll(filteredList);
    return null;
  }

  /**
   * Renders links
   */
  public void renderActionLinks()
  {
    int i = 0;
    wikiContext.append("<ul>");
    for (final String actionLink : getActionLinks()) {
      GWikiElement el = wikiContext.getWikiWeb().findElement(actionLink);
      if (el == null) {
        continue;
      }
      if (wikiContext.getWikiWeb().getAuthorization().isAllowToView(wikiContext, el.getElementInfo()) == false) {
        continue;
      }
      GWikiElementInfo info = el.getElementInfo();
      String title = info.getProps().getStringValue(GWikiPropKeys.TITLE);
      if (title.startsWith("I{") == true) {
        title = wikiContext.getTranslatedProp(title);
      }
      String link = wikiContext.renderExistingLinkWithAttr(el.getElementInfo(), title, "", "id", "actionLink" + i);
      try {
        renderFancyBox(wikiContext, "actionLink" + String.valueOf(i++));
      } catch (UnsupportedEncodingException ex) {
        GWikiLog.error("Error rendering actionLink", ex);
      }
      wikiContext.append("<li>").append(link).append("</li>");
    }
    wikiContext.append("</ul>");
    wikiContext.flush();
  }

  /**
   * @param ctx
   * @throws UnsupportedEncodingException
   */
  private void renderFancyBox(final GWikiContext ctx, String id) throws UnsupportedEncodingException
  {
    int width = 1000;
    int height = 700;

    ctx.append("\n<script type=\"text/javascript\">\njQuery(document).ready(function() {\n"
        + "$(\"#"
        + URLEncoder.encode(id, "UTF-8")
        + "\").fancybox({\n"
        + "width: "
        + width
        + ",\n"
        + "height: "
        + height
        + ",\n"
        + "type: 'iframe'\n"
        + "});\n"
        + "});\n"
        + "</script>\n");
  }

  /**
   * @param selectedPageId the selectedPageId to set
   */
  public void setSelectedPageId(String selectedPageId)
  {
    this.selectedPageId = selectedPageId;
  }

  /**
   * @return the selectedPageId
   */
  public String getSelectedPageId()
  {
    return selectedPageId;
  }

  /**
   * @param selectedTenant the selectedTenant to set
   */
  public void setSelectedTenant(String selectedTenant)
  {
    this.selectedTenant = selectedTenant;
  }

  /**
   * @return the selectedTenant
   */
  public String getSelectedTenant()
  {
    return selectedTenant;
  }

  public String getCurrentTenant()
  {
    GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    if (wikiSelector == null) {
      return StringUtils.EMPTY;
    }
    return wikiSelector.getTenantId(GWikiServlet.INSTANCE, wikiContext.getRequest());
  }

  /**
   * @param contentMap the contentMap to set
   */
  public void setContent(List<FileInfoWrapper> contentMap)
  {
    this.content = contentMap;
  }

  /**
   * @return the contentMap
   */
  public List<FileInfoWrapper> getContent()
  {
    return content;
  }

  /**
   * @param orderBy the orderBy to set
   */
  public void setOrderBy(String orderBy)
  {
    this.orderBy = orderBy;
  }

  /**
   * @return the orderBy
   */
  public String getOrderBy()
  {
    return orderBy;
  }

  /**
   * @param availableActionLinks the availableActionLinks to set
   */
  public void setActionLinks(List<String> availableActionLinks)
  {
    this.actionLinks = availableActionLinks;
  }

  /**
   * @return the availableActionLinks
   */
  public List<String> getActionLinks()
  {
    return actionLinks;
  }

  /**
   * @return the titleFilter
   */
  public String getTitleFilter()
  {
    return titleFilter;
  }

  /**
   * @param titleFilter the titleFilter to set
   */
  public void setTitleFilter(String titleFilter)
  {
    this.titleFilter = titleFilter;
  }

  /**
   * @return the catFilter
   */
  public String getCatFilter()
  {
    return catFilter;
  }

  /**
   * @param catFilter the catFilter to set
   */
  public void setCatFilter(String catFilter)
  {
    this.catFilter = catFilter;
  }

  /**
   * @return the authorFilter
   */
  public String getAuthorFilter()
  {
    return authorFilter;
  }

  /**
   * @param authorFilter the authorFilter to set
   */
  public void setAuthorFilter(String authorFilter)
  {
    this.authorFilter = authorFilter;
  }

  /**
   * @return the statusFilter
   */
  public String getStatusFilter()
  {
    return statusFilter;
  }

  /**
   * @param statusFilter the statusFilter to set
   */
  public void setStatusFilter(String statusFilter)
  {
    this.statusFilter = statusFilter;
  }

  /**
   * @return the dateFilter
   */
  public String getDateFilter()
  {
    return dateFilter;
  }

  /**
   * @param dateFilter the dateFilter to set
   */
  public void setDateFilter(String dateFilter)
  {
    this.dateFilter = dateFilter;
  }

  /**
   * @return the releaseFilter
   */
  public String getReleaseFilter()
  {
    return releaseFilter;
  }

  /**
   * @param releaseFilter the releaseFilter to set
   */
  public void setReleaseFilter(String releaseFilter)
  {
    this.releaseFilter = releaseFilter;
  }

  /**
   * @return the assigneeFilter
   */
  public String getAssigneeFilter()
  {
    return assigneeFilter;
  }

  /**
   * @param assigneeFilter the assigneeFilter to set
   */
  public void setAssigneeFilter(String assigneeFilter)
  {
    this.assigneeFilter = assigneeFilter;
  }

  public Map<String, String> getStates()
  {
    Map<String, String> map = new HashMap<String, String>();
    for (FileState state : FileState.values()) {
      map.put(state.name(), wikiContext.getTranslated(state.name()));
    }
    return map;
  }

  public Map<String, String> getReleases()
  {
    Map<String, String> map = new HashMap<String, String>();
    for (String tenant : getWikiSelector().getMptIdSelector().getTenants(GWikiWeb.getRootWiki())) {
      map.put(tenant, tenant);
    }
    return map;
  }

  public Map<String, String> getUsers()
  {
    final Matcher<String> m = new BooleanListRulesFactory<String>().createMatcher("admin/user/*");
    final List<GWikiElementInfo> userInfos = wikiContext.getElementFinder().getPageInfos(new GWikiPageIdMatcher(wikiContext, m));
    Map<String, String> map = new HashMap<String, String>();
    for (final GWikiElementInfo user : userInfos) {
      map.put(GWikiContext.getNamePartFromPageId(user.getId()), GWikiContext.getNamePartFromPageId(user.getId()));
    }
    return map;
  }

  /**
   * @param viewFilter the viewFilter to set
   */
  public void setViewFilter(String viewFilter)
  {
    this.viewFilter = viewFilter;
  }

  /**
   * @return the viewFilter
   */
  public String getViewFilter()
  {
    return viewFilter;
  }

  /**
   * Comparetor for ordering files
   * 
   * @author Stefan Stuetzer (s.stuetzer@micromata.com)
   */
  class FileInfoComparator implements Comparator<FileInfoWrapper>
  {
    private String orderBy;

    public FileInfoComparator(final String orderBy)
    {
      this.orderBy = orderBy;
    }

    public int compare(FileInfoWrapper o1, FileInfoWrapper o2)
    {
      if ("RELEASE_DATE".equals(this.orderBy) == true) {
        if (o1.getStartAt() == null && o2.getStartAt() == null) {
          return 0;
        }
        if (o1.getStartAt() == null) {
          return 1;
        }
        if (o2.getStartAt() == null) {
          return -1;
        }
        return o1.getStartAt().before(o2.getStartAt()) == true ? 1 : -1;
      }
      return 0;
    }
  }

  /**
   * Predicate for filtering files
   * 
   * @author Stefan Stuetzer (s.stuetzer@micromata.com)
   */
  class FileInfoFilterPredicate implements Predicate<FileInfoWrapper>
  {
    public boolean evaluate(final FileInfoWrapper o)
    {
      // check title filter
      if (StringUtils.isNotBlank(getTitleFilter()) == true
          && o.getElementInfo().getTitle().toLowerCase().contains(getTitleFilter().toLowerCase()) == false) {
        return false;
      }

      // check category filter
      if (StringUtils.isNotBlank(getCatFilter()) == true
          && o.getCategoryString().toLowerCase().contains(getCatFilter().toLowerCase()) == false) {
        return false;
      }

      // check author filter
      if (StringUtils.isNotBlank(getAuthorFilter()) == true
          && o.getElementInfo().getCreatedBy().toLowerCase().contains(getAuthorFilter().toLowerCase()) == false) {
        return false;
      }

      // check state filter
      if (StringUtils.isNotBlank(getStatusFilter()) == true
          && o.getFileStats().getFileState() != FileState.valueOf(getStatusFilter()) == true) {
        return false;
      }

      // check content release filter
      if (StringUtils.isNotBlank(getReleaseFilter()) == true && StringUtils.equalsIgnoreCase(o.getBranch(), getReleaseFilter()) == false) {
        return false;
      }

      // check assignee filter
      if (StringUtils.isNotBlank(getAssigneeFilter()) == true
          && StringUtils.equalsIgnoreCase(o.getFileStats().getAssignedTo(), getAssigneeFilter()) == false) {
        return false;
      }
      return true;
    }
  }
}
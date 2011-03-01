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
package de.micromata.genome.gwiki.pagelifecycle_1_0.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.GWikiBranchFileStatsArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class ViewBranchContentActionBean extends PlcActionBeanBase
{
  /**
   * Map tenant-id -> Map of containing elements
   */
  private Map<String, Map<GWikiElementInfo, FileStatsDO>> contentMap = new HashMap<String, Map<GWikiElementInfo, FileStatsDO>>();

  /**
   * blacklist of files which not should be considered in content list
   */
  private Matcher<String> blackListMatcher = new BooleanListRulesFactory<String>().createMatcher("*intern/*,*admin/*");

  private String selectedPageId;

  private String selectedTenant;

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

  public Object onReviewCreator()
  {
    setFileStatus(FileState.TO_REVIEW);
    updateList();
    return null;
  }

  public Object onRejectChiefEditor()
  {
    setFileStatus(FileState.DRAFT);
    updateList();
    return null;
  }

  public Object onApproveChiefEditor()
  {
    setFileStatus(FileState.APPROVED_CHIEF_EDITOR);
    updateList();
    return null;
  }

  public Object onRejectContentAdmin()
  {
    setFileStatus(FileState.TO_REVIEW);
    updateList();
    return null;
  }

  public Object onReleaseContentAdmin()
  {
    setFileStatus(FileState.APPROVED_CONTENT_ADMIN);
    updateList();
    return null;
  }

  public Object onEnterTenant()
  {
    GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    if (wikiSelector == null) {
      wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.entertenant");
      updateList();
      return null;
    }

    String tenant = getSelectedTenant();
    if (StringUtils.isBlank(tenant) == true) {
      wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.entertenant");
      updateList();
      return null;
    }
    wikiSelector.enterTenant(wikiContext, tenant);
    updateList();
    return null;
  }

  public Object onLeaveTenant()
  {
    GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    if (wikiSelector == null) {
      wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.leavetenant");
      updateList();
      return null;
    }
    wikiSelector.leaveTenant(wikiContext);
    updateList();
    return null;
  }

  public Object renderInfo(final String tenant)
  {
    GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    if (wikiSelector == null) {
      wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.loadtenantcontent");
      return null;
    }
    List<String> tenants = wikiSelector.getMptIdSelector().getTenants(GWikiWeb.getRootWiki());
    if (tenants == null || tenants.size() == 0 || tenants.contains(tenant) == false) {
      return null;
    }

    wikiContext.runInTenantContext(tenant, wikiSelector, new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        // if no branch filestats present only consider element infos
        GWikiElement branchInfo = wikiContext.getWikiWeb().findElement("admin/branch/intern/BranchInfoElement");
        if (branchInfo == null || branchInfo.getMainPart() == null) {
          return null;
        }

        GWikiArtefakt< ? > artefakt = branchInfo.getMainPart();
        if (artefakt instanceof GWikiPropsArtefakt == false) {
          return null;
        }

        GWikiPropsArtefakt infoArtefakt = (GWikiPropsArtefakt) artefakt;
        GWikiProps props = infoArtefakt.getCompiledObject();

        wikiContext.append("<div style=\"font-size:0.6em;size:0.6em;border:1px dashed;width:50%;\" \">");
        wikiContext.append("<table width=\"100%\" cellspacing=\"0\">");
        for (Entry<String, String> e : props.getMap().entrySet()) {
          wikiContext.append("<tr>");
          wikiContext.append("<td width=\"15%\">").append(e.getKey()).append("</td>");
          wikiContext.append("<td>").append(e.getValue()).append("</td>");
          wikiContext.append("</tr>");
        }
        wikiContext.append("</table>");
        wikiContext.append("</div>").append("<br/>");
        return null;

      }
    });
    return null;

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

    for (final String tenant : tenants) {
      wikiContext.runInTenantContext(tenant, wikiSelector, new CallableX<Void, RuntimeException>() {
        public Void call() throws RuntimeException
        {
          if (getContentMap().get(tenant) == null) {
            getContentMap().put(tenant, new HashMap<GWikiElementInfo, FileStatsDO>());
          }

          // get all elemtinfos of tenant
          List<GWikiElementInfo> tenantContent = wikiContext.getElementFinder().getPageInfos(new MatcherBase<GWikiElementInfo>() {
            private static final long serialVersionUID = -6020166500681070082L;

            public boolean match(GWikiElementInfo ei)
            {
              String tid = ei.getProps().getStringValue(GWikiPropKeys.TENANT_ID);
              return StringUtils.equals(tenant, tid);
            }
          });

          // if no branch filestats present only consider element infos
          GWikiElement branchFileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
          if (branchFileStats == null || branchFileStats.getMainPart() == null) {
            for (GWikiElementInfo ei : tenantContent) {
              getContentMap().get(tenant).put(ei, new FileStatsDO());
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
              getContentMap().get(tenant).put(ei, new FileStatsDO());
            } else {
              getContentMap().get(tenant).put(ei, fileStatsDO);
            }
          }
          return null;
        }
      });
    }
    return null;
  }

  /**
   * @param fileState
   * 
   */
  private void setFileStatus(final FileState fileState)
  {
    final String pageToApprove = getSelectedPageId();
    String tenant = getSelectedTenant();

    GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    if (wikiSelector == null) {
      return;
    }

    wikiContext.runInTenantContext(tenant, wikiSelector, new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
        if (fileStats == null || fileStats.getMainPart() == null) {
          wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.setstate");
          return null;
        }

        GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
        if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
          wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.setstate");
          return null;
        }

        GWikiBranchFileStatsArtefakt branchFilestatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
        BranchFileStats pageStats = branchFilestatsArtefakt.getCompiledObject();
        FileStatsDO fileStatsForPage = pageStats.getFileStatsForId(pageToApprove);
        if (fileStatsForPage == null) {
          wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.setstate");
          return null;
        }

        fileStatsForPage.setFileState(fileState);
        fileStatsForPage.setLastModifiedAt(GWikiProps.formatTimeStamp(new Date()));
        fileStatsForPage.setLastModifiedBy(wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));
        branchFilestatsArtefakt.setStorageData(pageStats.toString());

        wikiContext.getWikiWeb().saveElement(wikiContext, fileStats, true);

        return null;
      }
    });
  }

  /**
   * @param contentMap the contentMap to set
   */
  public void setContentMap(Map<String, Map<GWikiElementInfo, FileStatsDO>> contentMap)
  {
    this.contentMap = contentMap;
  }

  /**
   * @return the contentMap
   */
  public Map<String, Map<GWikiElementInfo, FileStatsDO>> getContentMap()
  {
    return contentMap;
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
}

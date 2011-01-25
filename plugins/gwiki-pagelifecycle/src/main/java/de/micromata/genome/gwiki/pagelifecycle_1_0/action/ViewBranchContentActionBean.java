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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.GWikiBranchFileStatsArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class ViewBranchContentActionBean extends ActionBeanBase
{

  /**
   * location of filestats file in a tenant
   */
  private static final String FILE_STATS_LOCATION = "admin/branch/BranchFileStats";

  /**
   * blacklist of files which not should be considered in content list
   */
  private List<String> fileBlackList = Arrays.asList("BranchInfoElement", "BranchFileStats", "GlobalTextIndex");
  
  private String selectedPageId;

  private String selectedTenant;

  /**
   * Map tenant-id -> Map of containing elements
   */
  private Map<String, Map<GWikiElementInfo, FileStatsDO>> contentMap = new HashMap<String, Map<GWikiElementInfo, FileStatsDO>>();

  @Override
  public Object onInit()
  {
    updateList();
    return null;
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
  
  public Object onEnterTenant() {
    GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    if (wikiSelector == null) {
      wikiContext.addSimpleValidationError("Cannot enter tenant");
      updateList();
      return null; 
    }
    
    String tenant = getSelectedTenant();
    if (StringUtils.isBlank(tenant) == true) {
      wikiContext.addSimpleValidationError("Cannot enter tenant");
      updateList();
      return null; 
    }
    wikiSelector.enterTenant(wikiContext, tenant);
    updateList();
    return null;
  }

  /**
   * 
   */
  private Object updateList()
  {
    GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    List<String> tenants = wikiSelector.getMptIdSelector().getTenants(GWikiWeb.getRootWiki());
    if (tenants == null || tenants.size() == 0) {
      return null;
    }

    for (final String tenant : tenants) {
      runInTenantContext(tenant, wikiSelector, new CallableX<Void, RuntimeException>() {
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
          GWikiElement branchFileStats = wikiContext.getWikiWeb().findElement(FILE_STATS_LOCATION);
          if (branchFileStats == null || branchFileStats.getMainPart() == null) {
            for (GWikiElementInfo ei : tenantContent) {
              getContentMap().get(tenant).put(ei, new FileStatsDO());
            }
            return null;
          }

          // collecting filestats information for each item
          for (GWikiElementInfo ei : tenantContent) {
            // ignore blacklisted files
            if (fileBlackList.contains(StringUtils.substringAfterLast(ei.getId(), "/")) == true) {
              continue;
            }
            
            
            GWikiArtefakt< ? > artefakt = branchFileStats.getMainPart();
            if (artefakt instanceof GWikiBranchFileStatsArtefakt) {
              GWikiBranchFileStatsArtefakt branchArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
              BranchFileStats fileStats = branchArtefakt.getCompiledObject();
              FileStatsDO fileStatsDO = fileStats.getFileStatsForId(ei.getId()); // cannot be null
              getContentMap().get(tenant).put(ei, fileStatsDO);
            } else {
              getContentMap().get(tenant).put(ei, new FileStatsDO());
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

    runInTenantContext(tenant, wikiSelector, new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        GWikiElement fileStats = wikiContext.getWikiWeb().findElement(FILE_STATS_LOCATION);
        if (fileStats == null || fileStats.getMainPart() == null) {
          wikiContext.addSimpleValidationError("Error setting state");
          return null;
        }

        GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
        if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
          wikiContext.addSimpleValidationError("Error setting state");
          return null;
        }

        GWikiBranchFileStatsArtefakt branchFilestatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
        BranchFileStats pageStats = branchFilestatsArtefakt.getCompiledObject();
        FileStatsDO fileStatsForPage = pageStats.getFileStatsForId(pageToApprove);
        if (fileStatsForPage == null) {
          wikiContext.addSimpleValidationError("Error setting state");
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
   * runs the callback code inside specified tenant
   * 
   * @param tenantId
   * @param wikiSelector
   * @param callBack
   * @return
   */
  private Void runInTenantContext(String tenantId, GWikiMultipleWikiSelector wikiSelector, CallableX<Void, RuntimeException> callBack)
  {
    String currentTenant = null;
    try {
      currentTenant = wikiSelector.getTenantId(GWikiServlet.INSTANCE, wikiContext.getRequest());
      if (StringUtils.equals(currentTenant, tenantId) == false) {
        wikiSelector.enterTenant(wikiContext, tenantId);
      }
      return callBack.call();
    } finally {
      // switch to previous tenant
      if (StringUtils.equals(currentTenant, tenantId) == true) {
        return null;
      }
      if (StringUtils.isBlank(currentTenant) == true) {
        wikiSelector.leaveTenant(wikiContext);
      } else {
        wikiSelector.enterTenant(wikiContext, currentTenant);
      }
    }
  }

  private GWikiMultipleWikiSelector getWikiSelector()
  {
    GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null) {
      wikiContext.addSimpleValidationError("No multiple branches supported");
      return null;
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == true) {
      GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
      return multipleSelector;
    }
    return null;
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
}

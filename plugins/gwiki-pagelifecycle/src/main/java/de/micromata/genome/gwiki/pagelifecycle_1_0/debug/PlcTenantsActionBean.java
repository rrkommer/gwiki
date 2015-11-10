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
package de.micromata.genome.gwiki.pagelifecycle_1_0.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMptIdSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.scheduler_1_0.api.GWikiScheduler;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.matcher.MatcherBase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PlcTenantsActionBean extends ActionBeanBase
{
  private String currentTenant;

  private List<String> availableTenants;

  private String selectedTenant;

  private List<String> tenantPageIds;

  public void init()
  {
    GWikiMptIdSelector idsel = getTenantSelector();
    if (idsel == null) {
      currentTenant = null;
      return;
    }
    currentTenant = StringUtils.defaultString(idsel.getTenantId(GWikiServlet.INSTANCE, wikiContext.getRequest()));
    if (StringUtils.isEmpty(currentTenant) == true) {
      currentTenant = null;
    }
  }

  public Object onInit()
  {
    init();
    return null;
  }

  protected GWikiMptIdSelector getTenantSelector()
  {
    GWikiWikiSelector ws = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if ((ws instanceof GWikiMultipleWikiSelector) == false) {
      return null;
    }
    GWikiMultipleWikiSelector ms = (GWikiMultipleWikiSelector) ws;
    return ms.getMptIdSelector();
  }

  public Object onListTenants()
  {
    init();
    GWikiMptIdSelector sel = getTenantSelector();
    if (sel == null) {
      wikiContext.addSimpleValidationError("No tenant available");
      return null;
    }
    availableTenants = sel.getTenants(GWikiWeb.getRootWiki());
    return null;
  }

  public Object onEnterTenant()
  {
    init();
    if (StringUtils.isBlank(selectedTenant) == true) {
      selectedTenant = null;
    }
    wikiContext.getWikiWeb().getDaoContext().getWikiSelector().enterTenant(wikiContext, selectedTenant);
    init();
    return null;
  }

  public Object onClearTenantCache()
  {
    init();
    if (StringUtils.isBlank(selectedTenant) == true) {
      return null;
    }
    wikiContext.getWikiWeb().getDaoContext().getWikiSelector().clearTenant(wikiContext, selectedTenant);
    return null;
  }

  public Object onShowTenantPageIds()
  {
    init();
    if (StringUtils.isEmpty(currentTenant) == true) {
      wikiContext.addSimpleValidationError("No tenant selected");
      return null;
    }
    List<String> pids = new ArrayList<String>();
    for (GWikiElementInfo ei : wikiContext.getElementFinder().getPageInfos(new MatcherBase<GWikiElementInfo>() {
      public boolean match(GWikiElementInfo ei)
      {
        String tid = ei.getProps().getStringValue(GWikiPropKeys.TENANT_ID);
        return StringUtils.equals(currentTenant, tid) == true;
      }
    })) {
      pids.add(ei.getId());
    }
    tenantPageIds = pids;
    return null;
  }

  public Object onSubmitTestJob()
  {
    Map<String, String> sm = new HashMap<String, String>();
    sm.put("A1", "V1");
    sm.put("A2", "V2");
    GWikiScheduler.submitJob(null, TestPubJob.class.getName(), "+10000", sm);
    return null;
  }

  public String getCurrentTenant()
  {
    return currentTenant;
  }

  public void setCurrentTenant(String currentTenant)
  {
    this.currentTenant = currentTenant;
  }

  public List<String> getAvailableTenants()
  {
    return availableTenants;
  }

  public void setAvailableTenants(List<String> availableTenants)
  {
    this.availableTenants = availableTenants;
  }

  public String getSelectedTenant()
  {
    return selectedTenant;
  }

  public void setSelectedTenant(String selectedTenant)
  {
    this.selectedTenant = selectedTenant;
  }

  public List<String> getTenantPageIds()
  {
    return tenantPageIds;
  }

  public void setTenantPageIds(List<String> tenantPageIds)
  {
    this.tenantPageIds = tenantPageIds;
  }
}

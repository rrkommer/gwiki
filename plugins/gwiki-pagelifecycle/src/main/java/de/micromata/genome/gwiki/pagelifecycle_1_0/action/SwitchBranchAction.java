//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.pagelifecycle_1_0.action;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * Simple Action for switching a branch
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class SwitchBranchAction extends ActionBeanBase
{
  private String newBranchId;

  private String backUrl;

  @Override
  public Object onInit()
  {
    if (getNewBranchId() == null) {
      return goBack();
    }
    
    GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null) {
      return goBack();
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == true) {
      GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
      String currentTenant = multipleSelector.getTenantId(GWikiServlet.INSTANCE, wikiContext.getRequest());

      if (getNewBranchId().equalsIgnoreCase(currentTenant) == true) {
        return goBack();
      }
      multipleSelector.enterTenant(wikiContext, getNewBranchId());
    }
    return goBack();
  }

  private Object goBack()
  {
    if (StringUtils.isNotBlank(getBackUrl())) {
      return getBackUrl();
    }
    return wikiContext.getWikiWeb().getHomeElement(wikiContext);
  }

  /**
   * @param referer the referer to set
   */
  public void setBackUrl(String referer)
  {
    this.backUrl = referer;
  }

  /**
   * @return the referer
   */
  public String getBackUrl()
  {
    return backUrl;
  }

  /**
   * @param newBranchId the newBranchId to set
   */
  public void setNewBranchId(String newBranchId)
  {
    this.newBranchId = newBranchId;
  }

  /**
   * @return the newBranchId
   */
  public String getNewBranchId()
  {
    return newBranchId;
  }
}
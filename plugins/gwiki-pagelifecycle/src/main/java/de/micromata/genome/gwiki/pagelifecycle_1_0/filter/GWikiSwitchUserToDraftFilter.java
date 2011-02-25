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
package de.micromata.genome.gwiki.pagelifecycle_1_0.filter;

import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiUserLogonFilter;
import de.micromata.genome.gwiki.model.filter.GWikiUserLogonFilterEvent;
import de.micromata.genome.gwiki.model.filter.GWikiUserLogonFilterEvent.LoginState;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiReqSessionMptIdSelector;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.GWikiPlcRights;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcUtils;

/**
 * Filter which switches the context of current user to draft context
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class GWikiSwitchUserToDraftFilter implements GWikiUserLogonFilter
{
  public Void filter(GWikiFilterChain<Void, GWikiUserLogonFilterEvent, GWikiUserLogonFilter> chain, GWikiUserLogonFilterEvent event)
  {
    if (LoginState.Logout.equals(event.getLoginState()) == true) {
      event.getWikiContext().getRequest().getSession().removeAttribute(GWikiReqSessionMptIdSelector.MPT_KEY);
      return chain.nextFilter(event);
    }

    GWikiContext ctx = event.getWikiContext();
    if (ctx.isAllowTo(GWikiPlcRights.PLC_VIEW_ALL_BRANCHES.name()) == true) {
      return chain.nextFilter(event);
    }

    GWikiWikiSelector wikiSelector = ctx.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null) {
      return chain.nextFilter(event);
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == true) {
      GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
      multipleSelector.enterTenant(ctx, PlcConstants.DRAFT_ID);
      
      // on first switch we have to ensure that needed metafiles are present in branch
      PlcUtils.ensureDraftBranchMetaFiles(multipleSelector, ctx);
    }
    return chain.nextFilter(event);
  }
}

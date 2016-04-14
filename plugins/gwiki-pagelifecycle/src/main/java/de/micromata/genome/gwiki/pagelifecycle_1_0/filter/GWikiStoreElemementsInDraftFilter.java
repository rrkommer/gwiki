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

package de.micromata.genome.gwiki.pagelifecycle_1_0.filter;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilterEvent;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.GWikiPlcRights;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcUtils;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Filter that ensures the user in the draft context while storing new elements
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
@SuppressWarnings("unused")
public class GWikiStoreElemementsInDraftFilter implements GWikiStorageStoreElementFilter
{

  public Void filter(GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter> chain,
      GWikiStorageStoreElementFilterEvent event)
  {
    GWikiContext ctx = event.getWikiContext();

    // admins do not switch to draft by default
    if (ctx.isAllowTo(GWikiPlcRights.PLC_VIEW_ALL_BRANCHES.name()) == true) {
      return chain.nextFilter(event);
    }

    GWikiWikiSelector wikiSelector = ctx.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null) {
      return chain.nextFilter(event);
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == false) {
      return chain.nextFilter(event);
    }

    GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
    multipleSelector.enterTenant(ctx, PlcConstants.DRAFT_ID);
    PlcUtils.ensureDraftBranchMetaFiles(multipleSelector, ctx);
    return chain.nextFilter(event);
  }

}

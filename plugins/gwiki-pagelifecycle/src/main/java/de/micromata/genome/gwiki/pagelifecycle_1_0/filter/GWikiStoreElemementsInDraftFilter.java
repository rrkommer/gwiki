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
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Filter that ensures the user in the draft context while storing new elements
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class GWikiStoreElemementsInDraftFilter implements GWikiStorageStoreElementFilter
{

  public Void filter(GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter> chain,
      GWikiStorageStoreElementFilterEvent event)
  {
    GWikiContext ctx = event.getWikiContext();

    // admins do not switch to draft by default
    if (ctx.getWikiWeb().getAuthorization().isAllowTo(ctx, "*") == true) {
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

    ensureDraftBranchMetaFiles(multipleSelector, ctx);
    multipleSelector.enterTenant(ctx, PlcConstants.DRAFT_ID);
    return chain.nextFilter(event);
  }

  /**
   * Ensures that all required branch meta files are present. if not they will be created
   */
  private void ensureDraftBranchMetaFiles(final GWikiMultipleWikiSelector wikiSelector, final GWikiContext wikiContext)
  {
    wikiContext.runInTenantContext(PlcConstants.DRAFT_ID, wikiSelector, new CallableX<Void, RuntimeException>() {
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
          props.setStringValue("BRANCH_ID", PlcConstants.DRAFT_ID);
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

}

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

package de.micromata.genome.gwiki.pagelifecycle_1_0.model;

import static de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants.*;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.GWikiBranchFileStatsArtefakt;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Utility class for pagelifecycle functionality
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class PlcUtils
{
  /**
   * Ensures that all required draftbranch meta files are present. if not they will be created
   */
  public static void ensureDraftBranchMetaFiles(final GWikiMultipleWikiSelector wikiSelector, final GWikiContext wikiContext)
  {
    ensureBranchMetaFiles(DRAFT_ID, wikiSelector, wikiContext);
  }

  /**
   * Ensures that all required branch meta files are present. if not they will be created
   */
  public static void ensureBranchMetaFiles(final String brachId, final GWikiMultipleWikiSelector wikiSelector,
      final GWikiContext wikiContext)
  {
    wikiContext.runInTenantContext(brachId, wikiSelector, new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        // ensure filestats present
        final GWikiElement fileStats = wikiContext.getWikiWeb().findElement(FILE_STATS_LOCATION);
        if (fileStats == null) {
          final GWikiElement el = createFileStats(wikiContext);
          // because branchfilestats is located in /admin folder you need to be su to store/update that file
          wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {
            public Void call() throws RuntimeException
            {
              wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
              return null;
            }
          });
        }

        // ensure branchinfo present
        final GWikiElement infoElement = wikiContext.getWikiWeb().findElement(BRANCH_INFO_LOCATION);
        if (infoElement == null) {
          final GWikiElement el = createInfoElement(wikiContext, DRAFT_ID, "Draft branch", "20991291000000000", "");
          // because branchinfo is located in /admin folder you need to be su to store/update that file
          wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {
            public Void call() throws RuntimeException
            {
              wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
              return null;
            }
          });
        }
        return null;
      }
    });
  }

  /**
   * Creates a new info element. Caller have to run this code in tenant context of wanted tenant
   * 
   * @param wikiContext
   * @param branchId Id of the branch
   * @param desc Description of thje branch
   * @param releaseDate Release date of the branch in internal timestamp format
   * @param releaseEndDate Release date of the branch in internal timestamp format
   * @return The created branchinfo element
   */
  public static GWikiElement createInfoElement(final GWikiContext wikiContext, final String branchId, final String desc,
      final String releaseDate, final String releaseEndDate)
  {
    final GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, BRANCH_INFO_LOCATION, BRANCH_INFO_TEMPLATE_ID, "BranchInfo");
    final GWikiArtefakt< ? > artefakt = el.getMainPart();

    final GWikiPropsArtefakt art = (GWikiPropsArtefakt) artefakt;
    final GWikiProps props = art.getCompiledObject();
    props.setStringValue(BRANCH_INFO_BRANCH_ID, branchId);
    props.setStringValue(BRANCH_INFO_DESCRIPTION, desc);
    props.setStringValue(BRANCH_INFO_BRANCH_STATE, BranchState.OFFLINE.name()); // each branch is initailly offline
    props.setStringValue(BRANCH_INFO_RELEASE_DATE, releaseDate);
    props.setStringValue(BRANCH_INFO_RELEASE_END_DATE, releaseEndDate);
    return el;
  }

  /**
   * Creates a new branch filestats element. Caller have to run this code in tenant context of wanted tenant
   * 
   * @param wikiContext the context
   * @return The created branchfilestats element,
   */
  public static GWikiElement createFileStats(final GWikiContext wikiContext)
  {
    final GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, FILE_STATS_LOCATION, FILESTATS_TEMPLATE_ID, "Branch File Stats");
    return el;
  }

  /**
   * Gets the branchfilestats of current tenant. Caller have to run this code in tenant context of wanted tenant
   * 
   * @param wikiContext the context
   * @return The current branchfilestats instance, <code>null</code> if not found
   */
  public static BranchFileStats getBranchFileStats(final GWikiContext ctx)
  {
    GWikiElement fileStats = ctx.getWikiWeb().findElement(FILE_STATS_LOCATION);
    if (fileStats == null || fileStats.getMainPart() == null) {
      return null;
    }
    GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
    if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
      return null;
    }
    GWikiBranchFileStatsArtefakt branchFilestatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
    return branchFilestatsArtefakt.getCompiledObject();
  }

  /**
   * Gets the branchinfo compiled artefakt of current tenant. Caller have to run this code in tenant context of wanted tenant
   * 
   * @param wikiContext the context
   * @return The current GwikiProps instance containing branchInfo meta data, <code>null</code> if not found
   */
  public static GWikiProps getBranchInfo(final GWikiContext ctx)
  {
    GWikiElement branchInfoElement = ctx.getWikiWeb().findElement(BRANCH_INFO_LOCATION);
    if (branchInfoElement == null) {
      return null;
    }
    GWikiArtefakt< ? > artefakt = branchInfoElement.getMainPart();
    if (artefakt instanceof GWikiPropsArtefakt == false) {
      return null;
    }
    GWikiPropsArtefakt propsArtefakt = (GWikiPropsArtefakt) artefakt;
    return propsArtefakt.getCompiledObject();
  }

}
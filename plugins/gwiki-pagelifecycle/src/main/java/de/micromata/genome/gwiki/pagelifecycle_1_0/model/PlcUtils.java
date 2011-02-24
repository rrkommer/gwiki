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
package de.micromata.genome.gwiki.pagelifecycle_1_0.model;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.GWikiContext;
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
    ensureBranchMetaFiles(PlcConstants.DRAFT_ID, wikiSelector, wikiContext);
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
        final GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
        if (fileStats == null) {
          final GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, PlcConstants.FILE_STATS_LOCATION,
              PlcConstants.FILESTATS_TEMPLATE_ID, "Branch File Stats");
          
          // because branchfilestats is located in /admin folder you need to be su to store/update that file
          wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {
            public Void call() throws RuntimeException
            {
              wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
              return null;
            }});
        }

        // ensure branchinfo present
        final GWikiElement infoElement = wikiContext.getWikiWeb().findElement(PlcConstants.BRANCH_INFO_LOCATION);
        if (infoElement == null) {
          final GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, PlcConstants.BRANCH_INFO_LOCATION,
              PlcConstants.BRANCH_INFO_TEMPLATE_ID, "BranchInfo");
          final GWikiArtefakt< ? > artefakt = el.getMainPart();

          final GWikiPropsArtefakt art = (GWikiPropsArtefakt) artefakt;
          final GWikiProps props = art.getCompiledObject();
          props.setStringValue("BRANCH_ID", PlcConstants.DRAFT_ID);
          props.setStringValue("DESCRIPTION", "Draft branch");
          props.setStringValue("BRANCH_STATE", "OFFLINE");
          props.setStringValue("RELEASE_DATE", "");
          props.setStringValue("RELEASE_END_DATE", "");

          // because branchinfo is located in /admin folder you need to be su to store/update that file
          wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {
            public Void call() throws RuntimeException
            {
              wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
              return null;
            }});
        }
        return null;
      }
    });
  }
}
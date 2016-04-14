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
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiStorageDeleteElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageDeleteElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.GWikiBranchFileStatsArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcUtils;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Filter which removes page entries from filestats file when they are deleted
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class GWikiDeleteFileStatsFilter implements GWikiStorageDeleteElementFilter
{
  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiStorageDeleteElementFilterEvent, GWikiStorageDeleteElementFilter> chain,
      GWikiStorageDeleteElementFilterEvent event)
  {
    final GWikiContext ctx = event.getWikiContext();
    GWikiElement elementToDelete = event.getElement();

    // if you update filestats it will be deleten and stored again. --> loop
    if (PlcConstants.FILE_STATS_LOCATION.equals(elementToDelete.getElementInfo().getId()) == true) {
      return chain.nextFilter(event);
    }
    
    final GWikiElement fileStats = ctx.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);

    // if we are not in a branch context
    if (fileStats == null) {
      return chain.nextFilter(event);
    }

    synchronized (fileStats.getElementInfo()) {
      GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
      if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
        return chain.nextFilter(event);
      }
      GWikiBranchFileStatsArtefakt fileStatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
      BranchFileStats fileStatsContent = fileStatsArtefakt.getCompiledObject();

      // if page already removed from fielstats file continue  
      if (fileStatsContent.isPagePresent(elementToDelete.getElementInfo().getId()) == false) {
        return chain.nextFilter(event);
      }
      fileStatsContent.removePage(elementToDelete.getElementInfo().getId());
      fileStatsArtefakt.setStorageData(fileStatsContent.toString());

      // because filestats is located in /admin folder you need to be su to store/update that file
      ctx.getWikiWeb().getAuthorization().runAsSu(ctx, new CallableX<Void, RuntimeException>() {
        public Void call() throws RuntimeException
        {
          ctx.getWikiWeb().saveElement(ctx, fileStats, false);
          return null;
        }
      });
    }
    return chain.nextFilter(event);
  }
}

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

import java.util.Date;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.GWikiBranchFileStatsArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class GWikiUpdateFileStatsFilter implements GWikiStorageStoreElementFilter
{

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter> chain,
      GWikiStorageStoreElementFilterEvent event)
  {
    GWikiContext wikiContext = event.getWikiContext();
    GWikiElement storedElement = event.getElement();
    GWikiElementInfo storedElementInfo = storedElement.getElementInfo();

    GWikiElement fileStats = wikiContext.getWikiWeb().findElement("admin/branch/BranchFileStats");

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

      FileStatsDO fileStatsForId = fileStatsContent.getFileStatsForId(storedElementInfo.getId());
      // if page already contained in fielstats file continue
      if (fileStatsForId != null) {
        return chain.nextFilter(event);
      }

      FileStatsDO newFileStat = new FileStatsDO();
      newFileStat.setPageId(storedElementInfo.getId());
      newFileStat.setFileState(FileState.CREATED);
      newFileStat.setCreatedAt(new Date().toString());
      newFileStat.setCreatedBy(wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));
      fileStatsContent.addFileStats(newFileStat);

      fileStatsArtefakt.setStorageData(fileStatsContent.toString());
      wikiContext.getWikiWeb().saveElement(wikiContext, fileStats, false);
    }

    return chain.nextFilter(event);
  }

}

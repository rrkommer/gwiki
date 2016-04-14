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

package de.micromata.genome.gwiki.plugin.vfolder_1_0;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilter;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFolderLoadFilter implements GWikiLoadElementInfosFilter
{
  public static final String VFILE_METATEMPLATEID = "admin/templates/VFolderMetaTemplate";

  protected List<GWikiElementInfo> onLoadVFolderElement(GWikiContext wikiContext, GWikiElementInfo ei)
  {
    GWikiElement el = wikiContext.getWikiWeb().getElement(ei);
    GWikiVFolderNode fn = GWikiVFolderNode.getVFolderFromElement(el);
    List<GWikiElementInfo> ell = GWikiVFolderUtils.loadFsElements(wikiContext, el, fn);
    return ell;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiLoadElementInfosFilterEvent, GWikiLoadElementInfosFilter> chain,
      GWikiLoadElementInfosFilterEvent event)
  {
    chain.nextFilter(event);
    List<GWikiElementInfo> newEls = new ArrayList<GWikiElementInfo>();
    for (GWikiElementInfo ei : event.getPageInfos().values()) {
      if (VFILE_METATEMPLATEID.equals(ei.getProps().getStringValue(GWikiPropKeys.WIKIMETATEMPLATE)) == true) {
        newEls.addAll(onLoadVFolderElement(event.getWikiContext(), ei));
      }
    }
    for (GWikiElementInfo nei : newEls) {
      event.getPageInfos().put(nei.getId(), nei);
    }
    return null;
  }

}

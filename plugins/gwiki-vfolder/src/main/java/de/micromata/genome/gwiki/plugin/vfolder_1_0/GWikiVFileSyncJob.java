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
package de.micromata.genome.gwiki.plugin.vfolder_1_0;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiSchedulerJobBase;
import de.micromata.genome.gwiki.model.matcher.GWikiElementMetatemplateMatcher;
import de.micromata.genome.util.types.Converter;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFileSyncJob extends GWikiSchedulerJobBase
{

  private static final long serialVersionUID = -6708504256721100472L;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiSchedulerJobBase#call()
   */
  @Override
  public void call()
  {
    List<String> pages;
    String pageIds = args.get("PAGEIDS");
    if (StringUtils.isEmpty(pageIds) == true) {
      pages = new ArrayList<String>();
      for (GWikiElementInfo ei : wikiContext.getElementFinder().getPageInfos(
          new GWikiElementMetatemplateMatcher(wikiContext, GWikiVFolderLoadFilter.VFILE_METATEMPLATEID))) {
        pages.add(ei.getId());
      }
    } else {
      pages = Converter.parseStringTokens(pageIds, ", ", false);
    }
    for (String page : pages) {
      check(page);
    }
  }

  public void check(String pageId)
  {
    GWikiElement el = wikiContext.getWikiWeb().findElement(pageId);
    if (el == null) {
      GWikiLog.warn("GWikiVFileSyncJob; cannot find pageId: " + pageId);
      return;
    }
    GWikiVFolderNode fn = GWikiVFolderNode.getVFolderFromElement(el);
    GWikiVFolderUtils.checkFolders(wikiContext, el, fn, true);
  }
}

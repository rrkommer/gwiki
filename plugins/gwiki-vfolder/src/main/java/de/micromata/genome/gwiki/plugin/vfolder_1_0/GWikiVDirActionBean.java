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

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVDirActionBean extends GWikiVDirOrFileActionBeanBase
{
  private List<GWikiElementInfo> files = new ArrayList<GWikiElementInfo>();

  public Object onInit()
  {
    init();
    String contextIndex = null;
    if (embedded == true) {
      String[] indexFiles = GWikiVFolderUtils.indexFileNames;

      for (String ifile : indexFiles) {
        String nid = pageId + "/" + ifile;
        GWikiElement chi = wikiContext.getWikiWeb().findElement(nid);
        if (chi == null) {
          continue;
        }
        contextIndex = chi.getElementInfo().getId();
        break;
      }
    }
    // embedded = false;

    GWikiElement cel = wikiContext.getCurrentElement();
    // GWikiElementInfo pid = cel.getElementInfo().getParent(wikiContext);
    List<GWikiElementInfo> alels = wikiContext.getElementFinder().getDirectChilds(cel.getElementInfo());
    for (GWikiElementInfo ei : alels) {
      if (GWikiVFolderUtils.VFILEMETATEMPLATE.equals(ei.getMetaTemplate().getPageId())) {
        files.add(ei);
      }
    }
    return contextIndex;
  }

  public List<GWikiElementInfo> getFiles()
  {
    return files;
  }

  public void setFiles(List<GWikiElementInfo> files)
  {
    this.files = files;
  }
}

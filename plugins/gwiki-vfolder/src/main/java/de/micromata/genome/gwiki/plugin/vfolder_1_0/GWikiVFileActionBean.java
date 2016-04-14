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

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.logging.GLog;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFileActionBean extends GWikiVDirOrFileActionBeanBase
{

  @Override
  public void init()
  {
    super.init();
  }

  @Override
  public Object onInit()
  {
    init();
    if (folderNode.isDirectAttachments() == true
        || StringUtils.equals(wikiContext.getRequestParameter("dl"), "true") == true) {
      return doDownload();
    }

    if (embedded == true) {

      if (folderNode.isHtmlVFile(pageId) == false) {
        try {
          GWikiVFolderUtils.writeContent(fvolderEl, pageId, wikiContext.getResponse());
        } catch (IOException ex) {
          GLog.note(GWikiLogCategory.Wiki, "Error writing attachment: " + pageId);
        }
        return noForward();
      }
      List<String> csse = folderNode.getAddCss();
      if (csse != null) {
        wikiContext.getRequiredCss().addAll(csse);
      }
      rawText = GWikiVFolderUtils.getHtmlBody(fvolderEl, folderNode, pageId);

    } else {
      rawText = wikiContext.getWikiWeb().getContentSearcher().getHtmlPreview(wikiContext, pageId);
    }

    return null;
  }

  public Object onDownload()
  {
    init();
    return doDownload();
  }

  protected Object doDownload()
  {
    try {
      GWikiVFolderUtils.writeContent(fvolderEl, pageId, wikiContext.getResponse());
    } catch (IOException ex) {
      GLog.note(GWikiLogCategory.Wiki, "Error writing attachment: " + pageId);
    }
    return noForward();
  }

}

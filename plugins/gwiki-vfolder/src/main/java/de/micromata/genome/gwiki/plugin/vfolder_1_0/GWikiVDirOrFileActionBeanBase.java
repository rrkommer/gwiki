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

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVDirOrFileActionBeanBase extends ActionBeanBase
{
  /** show content embedded (html) or not */
  protected boolean embedded = false;

  protected String pageId;

  protected GWikiElement fvolderEl;

  protected GWikiVFolderNode folderNode;

  protected String rawText;

  public void init()
  {
    pageId = wikiContext.getCurrentElement().getElementInfo().getId();

    fvolderEl = wikiContext.getCurrentElement();
    if (folderNode == null) {
      String vfid = wikiContext.getCurrentElement().getElementInfo().getProps().getStringValue(GWikiVFolderUtils.FVOLDER);
      if (vfid != null)
        fvolderEl = wikiContext.getWikiWeb().getElement(vfid);

    }
    if (fvolderEl != null) {
      folderNode = GWikiVFolderNode.getVFolderFromElement(fvolderEl);
      embedded = folderNode.isExtractBody();
    }
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public boolean isEmbedded()
  {
    return embedded;
  }

  public void setEmbedded(boolean embedded)
  {
    this.embedded = embedded;
  }

  public String getRawText()
  {
    return rawText;
  }

  public void setRawText(String rawText)
  {
    this.rawText = rawText;
  }
}

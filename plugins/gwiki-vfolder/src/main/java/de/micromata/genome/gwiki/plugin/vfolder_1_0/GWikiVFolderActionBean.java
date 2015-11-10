////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import java.util.List;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFolderActionBean extends GWikiVDirActionBean
{
  /**
   * if current directory has an index.html file.
   */
  private String contentIndex = null;

  public Object onInit()
  {
    Object index = super.onInit();
    if (embedded == true && index instanceof String) {

      contentIndex = (String) index;

      List<String> csse = folderNode.getAddCss();
      if (csse != null) {
        wikiContext.getRequiredCss().addAll(csse);
      }
      rawText = GWikiVFolderUtils.getHtmlBody(fvolderEl, folderNode, contentIndex);

    }
    return null;
  }

  public Object onScanFiles()
  {
    GWikiElement el = wikiContext.getCurrentElement();
    GWikiVFolderNode fn = GWikiVFolderNode.getVFolderFromElement(el);
    GWikiVFolderUtils.checkFolders(wikiContext, el, fn, false);
    return null;
  }

  public Object onScanIncrementelFiles()
  {
    GWikiElement el = wikiContext.getCurrentElement();
    GWikiVFolderNode fn = GWikiVFolderNode.getVFolderFromElement(el);
    GWikiVFolderUtils.checkFolders(wikiContext, el, fn, true);
    return null;
  }

  public Object onMountFs()
  {
    GWikiElement el = wikiContext.getCurrentElement();
    GWikiVFolderNode fn = GWikiVFolderNode.getVFolderFromElement(el);
    GWikiVFolderUtils.mountFs(wikiContext, el, fn);
    return onInit();
  }

  public Object onDismoutFs()
  {
    GWikiElement el = wikiContext.getCurrentElement();
    GWikiVFolderNode fn = GWikiVFolderNode.getVFolderFromElement(el);
    GWikiVFolderUtils.dismountFs(wikiContext, el, fn);
    return onInit();
  }

  public boolean includeStdIndex()
  {
    GWikiElement el = wikiContext.getCurrentElement();

    if (wikiContext.getWikiWeb().getAuthorization().isAllowToEdit(wikiContext, el.getElementInfo()) == true) {
      return false;
    }
    String parentPath = el.getElementInfo().getId();
    for (String idxf : GWikiVFolderUtils.indexFileNames) {
      String fqp = parentPath + "/" + idxf;
      if (wikiContext.getWikiWeb().findElementInfo(fqp) == null) {
        continue;
      }
      fqp = wikiContext.localUrl(fqp);

      wikiContext.append("<script language=\"JavaScript\">self.location=\"" + fqp + "\";</script>");
      return true;
    }
    return false;
  }

  public void renderTextContext()
  {
    GWikiElement el = wikiContext.getCurrentElement();
    GWikiArtefakt< ? > art = el.getPart("MainPage");
    if (art == null) {
      return;
    }
    if (art instanceof GWikiExecutableArtefakt< ? >) {
      ((GWikiExecutableArtefakt< ? >) art).render(wikiContext);
    } else {
      wikiContext.getWikiWeb().serveWiki(wikiContext, el);
    }
  }

  public String getContentIndex()
  {
    return contentIndex;
  }

  public void setContentIndex(String contentIndex)
  {
    this.contentIndex = contentIndex;
  }

}

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
public class GWikiVFolderActionBean extends ActionBeanBase
{

  public Object onInit()
  {
    return null;
  }

  public Object onScanFiles()
  {
    GWikiElement el = wikiContext.getCurrentElement();
    GWikiVFolderNode fn = GWikiVFolderNode.getVFolderFromElement(el);
    GWikiVFolderUtils.checkFolders(wikiContext, el, fn);
    return null;
  }

  public Object onMountFs()
  {
    GWikiElement el = wikiContext.getCurrentElement();
    GWikiVFolderNode fn = GWikiVFolderNode.getVFolderFromElement(el);
    GWikiVFolderUtils.mountFs(wikiContext, el, fn);
    return null;
  }

  public Object onDismoutFs()
  {
    GWikiElement el = wikiContext.getCurrentElement();
    GWikiVFolderNode fn = GWikiVFolderNode.getVFolderFromElement(el);
    GWikiVFolderUtils.dismountFs(wikiContext, el, fn);
    return null;
  }
}

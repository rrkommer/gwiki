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

import de.micromata.genome.gwiki.model.GWikiAbstractElement;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFileElement extends GWikiAbstractElement
{

  private static final long serialVersionUID = 5837756753247413770L;

  private String content;

  public GWikiVFileElement(GWikiElementInfo elementInfo)
  {
    super(elementInfo);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiElement#getMainPart()
   */
  public GWikiArtefakt< ? > getMainPart()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiElement#serve(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void serve(GWikiContext ctx)
  {
    // TODO check security
    if (content == null) {
      content = ctx.getWikiWeb().getStorage().getFileSystem().readTextFile("/" + elementInfo.getId());
    }
    ctx.append(content);
  }
}

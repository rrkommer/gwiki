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

import java.util.Map;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.GWikiIndexedArtefakt;
import de.micromata.genome.gwiki.utils.AppendableI;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFileArtefakt implements GWikiArtefakt<byte[]>, GWikiIndexedArtefakt
{

  private static final long serialVersionUID = 3773410146827927144L;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.search.GWikiIndexedArtefakt#getPreview(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.utils.AppendableI)
   */
  public void getPreview(GWikiContext ctx, AppendableI sb)
  {
    final GWikiElement el = ctx.getCurrentElement();
    if (el == null) {
      return;
    }
    GWikiVFolderUtils.getPreview(ctx, el, sb);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefakt#collectParts(java.util.Map)
   */
  public void collectParts(Map<String, GWikiArtefakt< ? >> map)
  {

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefakt#getCompiledObject()
   */
  public byte[] getCompiledObject()
  {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefakt#setCompiledObject(java.io.Serializable)
   */
  public void setCompiledObject(byte[] compiledObject)
  {

  }

}

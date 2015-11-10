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
package de.micromata.genome.gwiki.model;

import java.io.Serializable;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Delegates call to other part of other page.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiDelegateToOtherPartArtefakt<T extends Serializable> extends GWikiArtefaktBase<T> implements GWikiExecutableArtefakt<T>
{

  private static final long serialVersionUID = 8675127793426958733L;

  private String pageId;

  private String partName;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    GWikiArtefakt< ? > part = ctx.getWikiWeb().getElement(pageId).getPart(partName);
    if (part == null) {
      throw new RuntimeException("GWikiDelegateToOtherPartArtefakt; Cannot find part. PageId=" + pageId + " ;part=" + partName);
    }
    if ((part instanceof GWikiExecutableArtefakt< ? >) == false) {
      throw new RuntimeException("GWikiDelegateToOtherPartArtefakt; Other part is not executable. PageId=" + pageId + " ;part=" + partName);
    }
    GWikiExecutableArtefakt< ? > expart = (GWikiExecutableArtefakt< ? >) part;
    return expart.render(ctx);
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getPartName()
  {
    return partName;
  }

  public void setPartName(String partName)
  {
    this.partName = partName;
  }
}

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

package de.micromata.genome.gwiki.page.impl;

import java.util.Map;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiArtefaktBase;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.PropUtils;

/**
 * Delegate to other page.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiDelegateToPageExecutableArtefakt extends GWikiArtefaktBase<String> implements GWikiExecutableArtefakt<String>
{

  private static final long serialVersionUID = 1137927890215377164L;

  private String pageId;

  public GWikiDelegateToPageExecutableArtefakt()
  {

  }

  public GWikiDelegateToPageExecutableArtefakt(String pageId)
  {
    this.pageId = pageId;
  }

  public String getResolvePageId(final GWikiContext ctx)
  {
    return PropUtils.eval(pageId, "skin", ctx.getSkin());
  }

  public boolean renderWithParts(final GWikiContext ctx)
  {
    GWikiElement el = ctx.getWikiWeb().getElement(getResolvePageId(ctx));
    el.serve(ctx);
    return true;
  }

  @Override
  public void prepareHeader(GWikiContext wikiContext)
  {
    for (Map.Entry<String, GWikiArtefakt< ? >> me : parts.entrySet()) {
      GWikiArtefakt< ? > art = me.getValue();
      if (art instanceof GWikiExecutableArtefakt< ? >) {
        ((GWikiExecutableArtefakt< ? >) art).prepareHeader(wikiContext);
      }
    }
    GWikiElement el = wikiContext.getWikiWeb().getElement(getResolvePageId(wikiContext));
    el.prepareHeader(wikiContext);
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }
}

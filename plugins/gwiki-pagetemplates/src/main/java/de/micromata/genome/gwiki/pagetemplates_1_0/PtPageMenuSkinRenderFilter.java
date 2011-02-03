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
package de.micromata.genome.gwiki.pagetemplates_1_0;

import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilter;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilterEvent;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilterEvent.GuiElementType;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Filter for rendering separate menu
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class PtPageMenuSkinRenderFilter implements GWikiSkinRenderFilter
{

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain, de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiSkinRenderFilterEvent, GWikiSkinRenderFilter> chain, GWikiSkinRenderFilterEvent event)
  {
    if (GuiElementType.PageMenu.name().equals(event.getGuiElementType()) == true) {
      GWikiContext ctx = event.getWikiContext();
      ctx.append("<li class=\"gwikiMenuIcon\"");
      ctx.append("<span>");
      ctx.append("<a href=\"\" title=\"").append(ctx.getTranslated("gwiki.pt.common.edit")).append("\">");
      ctx.append("<img border=\"0\" src=\"/inc/gwiki/img/icons/article32.png\">");
      ctx.append("</a>");
      ctx.append("</span>");

      // TODO: take user roles?
      if (event.getWikiContext().isAllowTo("*EDITOR*")) {
        ctx.append("<ul>");
        ctx.append("<li>").append(ctx.renderLocalUrl("edit/pagetemplates/CreateArticleWizard"));
        ctx.append("</ul>");
        ctx.append("</li>");
      }
    }
    
    return null;
  }

}

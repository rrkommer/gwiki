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

import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.a;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.img;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.li;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.span;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.ul;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilter;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilterEvent;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilterEvent.GuiElementType;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * Filter for rendering separate menu
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class PtSkinRenderFilter implements GWikiSkinRenderFilter
{
  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiSkinRenderFilterEvent, GWikiSkinRenderFilter> chain, GWikiSkinRenderFilterEvent event)
  {
    final GWikiContext ctx = event.getWikiContext();
    if (ctx.isAllowTo(GWikiPtRights.PT_VIEW_MENU.name()) == false) {
      return chain.nextFilter(event);
    }

    if (GuiElementType.PageMenu.name().equals(event.getGuiElementType()) == true) {
      ctx.getWikiWeb().getI18nProvider().addTranslationElement(ctx, "edit/pagetemplates/i18n/PtI18N");

      XmlElement menu = li(new String[][] { { "class", "gwikiMenuIcon"}}, //
          span( //
          a(new String[][] { { "href", ""}, { "title", ctx.getTranslated("gwiki.pt.common.edit")}}, //
              img("border", "0", "src", "/inc/gwiki/img/icons/article32.png")) //
          ));

      // ul for menu entries
      XmlElement entryList = ul("name", "");

      // Wizard
      GWikiElement wiz = ctx.getWikiWeb().findElement("/edit/pagetemplates/PageWizard");
      if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, wiz.getElementInfo())) {
        entryList.nest(//
            li( //
                a(new String[][] { { "href", "/edit/pagetemplates/PageWizard"}}, // 
                text(ctx.getTranslated("gwiki.page.articleWizard.title")))
               ) //
        ); //
        menu.nest(entryList);
      }

      ctx.append(menu.toString());
    }
    return chain.nextFilter(event);
  }
}

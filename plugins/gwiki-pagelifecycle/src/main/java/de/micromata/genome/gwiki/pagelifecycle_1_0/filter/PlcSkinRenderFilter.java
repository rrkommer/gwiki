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
package de.micromata.genome.gwiki.pagelifecycle_1_0.filter;

import static de.micromata.genome.util.xml.xmlbuilder.html.Html.*;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.*;

import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilter;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilterEvent;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilterEvent.GuiElementType;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * Filter for rendering plugin dependent menu items for pagelifycycle option
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class PlcSkinRenderFilter implements GWikiSkinRenderFilter
{

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiSkinRenderFilterEvent, GWikiSkinRenderFilter> chain, GWikiSkinRenderFilterEvent event)
  {
    GWikiContext ctx = event.getWikiContext();

    // TODO stefan berechtigung korrekt setzen
    if (event.getWikiContext().isAllowTo("*EDITOR*") == false) {
      return chain.nextFilter(event);
    }
    
    if (GuiElementType.PageMenu.name().equals(event.getGuiElementType()) == true) {
      ctx.getWikiWeb().getI18nProvider().addTranslationElement(ctx, "edit/pagelifecycle/i18n/PlcI18N");

      XmlElement menu = li(new String[][] { { "class", "gwikiMenuIcon"}}, //
          span( //
            a(new String[][] { { "href", ""}, { "title", ""}}, //
                img("border", "0", "src", "/inc/gwiki/img/icons/monitor32.png")) //
          ));

      // ul for menu entries
      XmlElement entryList = ul("name", "");

      // render menu entries
      Matcher<String> m = new BooleanListRulesFactory<String>().createMatcher("edit/pagelifecycle/Plc*");
      List<GWikiElementInfo> pages = ctx.getElementFinder().getPageInfos(new GWikiPageIdMatcher(ctx, m));

      for (GWikiElementInfo page : pages) {
        if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, page)) {
          entryList.nest(
              li(
                  a(new String[][] { { "href", "/" + page.getId()}},
                      text(ctx.getTranslatedProp(page.getTitle())))
              ));
        }
      }
      
      menu.nest(entryList);
      ctx.append(menu.toString());
    }
    return chain.nextFilter(event);
  }
}

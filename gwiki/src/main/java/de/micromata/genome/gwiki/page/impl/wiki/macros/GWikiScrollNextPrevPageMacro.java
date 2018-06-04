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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * Render Next/Prev link.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "Renders Next/Previsous Page link",
    params = { @MacroInfoParam(name = "prevPage", type = MacroParamType.Boolean,
        info = "If true, renders a link to previous page, otherwise to next."),
        @MacroInfoParam(name = "title", info = "Title for the link") },
    renderFlags = { GWikiMacroRenderFlags.InTextFlow, GWikiMacroRenderFlags.RteSpan })
public class GWikiScrollNextPrevPageMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = -5506018415475453362L;

  private boolean prevPage;

  private String title;

  public GWikiScrollNextPrevPageMacro()
  {
    setRenderModesFromAnnot();
  }

  public GWikiScrollNextPrevPageMacro(String title, boolean prevPage)
  {
    this.title = title;
    this.prevPage = prevPage;
  }

  public static GWikiElementInfo getNextSilbling(GWikiContext wikiContext, GWikiElementByOrderComparator comparator)
  {
    return getSilbling(false, wikiContext, comparator);
  }

  public static GWikiElementInfo getPrevSilbling(GWikiContext wikiContext, GWikiElementByOrderComparator comparator)
  {
    return getSilbling(false, wikiContext, comparator);
  }

  public static GWikiElementInfo getSilbling(boolean prevPage, GWikiContext wikiContext,
      GWikiElementByOrderComparator comparator)
  {
    GWikiElement el = wikiContext.getCurrentElement();
    if (el == null) {
      return null;
    }
    GWikiElementInfo pif = el.getElementInfo().getParent(wikiContext);
    if (pif == null) {
      return null;
    }
    List<GWikiElementInfo> cl = wikiContext.getElementFinder().getDirectChilds(pif);
    if (comparator != null) {
      Collections.sort(cl, comparator);
    } else {
      Collections.sort(cl,
          new GWikiElementByChildOrderComparator(new GWikiElementByOrderComparator(new GWikiElementByIntPropComparator(
              "ORDER", 0))));
    }
    int curIdx = cl.indexOf(el.getElementInfo());
    if (curIdx == -1) {
      return null;
    }
    if (prevPage == true) {
      --curIdx;
      if (curIdx < 0) {
        return null;
      }
    } else {
      ++curIdx;
      if (curIdx >= cl.size()) {
        return null;
      }
    }
    GWikiElementInfo ninf = cl.get(curIdx);
    return ninf;
  }

  @Override
  public boolean renderImpl(GWikiContext wikiContext, MacroAttributes attrs)
  {
    GWikiElementInfo ninf = getSilbling(prevPage, wikiContext, null);
    if (ninf == null) {
      return true;
    }
    String lkTitle = title;
    if (StringUtils.isEmpty(lkTitle) == true) {
      lkTitle = wikiContext.getTranslatedProp(ninf.getTitle());
    }
    String addArgs = wikiContext.getRequestParameter("editMode");
    if (addArgs != null) {
      addArgs = "?editMode=true";
    }
    wikiContext.append(wikiContext.renderExistingLink(ninf, lkTitle, addArgs));
    return true;
  }

  public boolean isPrevPage()
  {
    return prevPage;
  }

  public void setPrevPage(boolean prevPage)
  {
    this.prevPage = prevPage;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

}

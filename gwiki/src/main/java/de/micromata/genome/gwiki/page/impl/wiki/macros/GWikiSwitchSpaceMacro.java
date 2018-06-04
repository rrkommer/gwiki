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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiSpaces;
import de.micromata.genome.gwiki.model.GWikiSpaces.SpaceInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;
import de.micromata.genome.gwiki.page.impl.wiki.filter.GWikiSpaceFilter;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@MacroInfo(info = "Switches to another space",
    params = { @MacroInfoParam(name = "space",
        info = "If given renders a link to Space, otherwise renders a dropdown with available spaces", enumValues = {
            "${ret = new java.util.ArrayList(); ret.add(\"\"); "
                + "ret.addAll(de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiSwitchSpaceMacro.getSpaces(gwikiContext)); "
                + "return ret;}" })
    },
    renderFlags = { GWikiMacroRenderFlags.InTextFlow, GWikiMacroRenderFlags.RteSpan })
public class GWikiSwitchSpaceMacro extends GWikiMacroBean
{
  private String space;

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {

    GWikiSpaces spaces = ctx.getWikiWeb().getSpaces();
    if (StringUtils.isNotBlank(space) == true) {
      GWikiElementInfo ei = spaces.findWelcomeForSpace(ctx, space);

      if (ei == null) {
        return true;
      } else {
        String turl = "window.location='"
            + ctx.escape(ctx.localUrl(ctx.getCurrentElement().getElementInfo().getId()))
            + "?" + GWikiSpaceFilter.WIKI_SET_SPACE_PARAM + "=" + space + "'";
        ctx.append("<a href='#' onclick=\"" + turl + "\">");
        String title = ctx.getTranslatedProp(ei.getTitle());
        ctx.appendEscText(title);
        ctx.append("</a>");
      }
    } else {
      List<SpaceInfo> list = ctx.getWikiWeb().getSpaces().getAvailableSpaces(ctx);
      if (list.isEmpty() == true) {
        return true;
      }
      ctx.append("<select class='gwikiSwitchSpace' onchange=\"window.location='"
          + ctx.escape(ctx.localUrl(ctx.getCurrentElement().getElementInfo().getId()))
          + "?" + GWikiSpaceFilter.WIKI_SET_SPACE_PARAM + "=' + this.value\">\n");
      ctx.append("<option value=''></option>\n");
      String curSpace = spaces.getUserCurrentSpaceId(ctx);
      for (SpaceInfo p : list) {
        String selected = "";
        if (StringUtils.equals(p.getSpaceId(), curSpace) == true) {
          selected = " selected='selected '";
        }
        ctx.append("<option " + selected + "value='" + ctx.escape(p.getSpaceId()) + "'>").appendEscText(p.getTitle())
            .append("</option>\n");
      }
      ctx.append("</select>");
    }
    return true;
  }

  public static List<String> getSpaces(GWikiContext ctx)
  {
    return ctx.getWikiWeb().getSpaces().getSpaceIds(ctx);
  }

  public String getSpace()
  {
    return space;
  }

  public void setSpace(String space)
  {
    this.space = space;
  }

}

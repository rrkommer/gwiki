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

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Represents p html element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentP extends GWikiFragmentChildsBase
{

  private static final long serialVersionUID = -8245596367479475761L;

  private String styleClass;
  private String style;

  public GWikiFragmentP()
  {
    // super("<p/>\n");
  }

  public GWikiFragmentP(List<GWikiFragment> childs)
  {
    super(childs);
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    if (StringUtils.isBlank(style) == true && StringUtils.isBlank(styleClass) == true) {
      sb.append("\n\n");
      getChildSouce(sb);
      return;
    }
    MacroAttributes mas = new MacroAttributes();
    mas.setCmd("p");
    if (StringUtils.isBlank(style) == false) {
      mas.getArgs().setStringValue("style", style);
    }
    if (StringUtils.isBlank(styleClass) == false) {
      mas.getArgs().setStringValue("styleClass", styleClass);
    }
    sb.append("{");
    mas.toHeadContent(sb);
    sb.append("}\n");
    getChildSouce(sb);
    sb.append("\n{p}\n");
  }

  // @Override
  @Override
  public boolean render(GWikiContext ctx)
  {
    ctx.append("<p");
    if (StringUtils.isNotBlank(styleClass) == true) {
      ctx.append(" class=\"").append(ctx.escape(styleClass)).append("\"");
    }
    if (StringUtils.isNotBlank(style) == true) {
      ctx.append(" style=\"").append(ctx.escape(style)).append("\"");
    }
    if (childs != null && childs.size() > 0) {
      ctx.append(">\n");
      renderChilds(ctx);
      ctx.append("</p>\n");
    } else {
      ctx.append("/>");
    }
    return true;
  }

  @Override
  public int getRenderModes()
  {
    return GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.ContainsTextBlock, GWikiMacroRenderFlags.NoWrapWithP);
  }

  public String getStyleClass()
  {
    return styleClass;
  }

  public void setStyleClass(String addClass)
  {
    this.styleClass = addClass;
  }

  public String getStyle()
  {
    return style;
  }

  public void setStyle(String style)
  {
    this.style = style;
  }

}

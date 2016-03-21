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
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * See also GWikiSectionMacro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(
    info = "The Macro column can be used in connection with Macro section to define a multiple column layout.<br/>"
        + "This Macro must be nested inside a section macro.",
    params = { @MacroInfoParam(name = "width", info = " Optional String, width (px, % or em) of the column"),
        @MacroInfoParam(name = "class", info = "Optional. CSS class attribute of the Column"),
        @MacroInfoParam(name = "style", info = "Optional. CSS style attribute"),
    },
    renderFlags = { GWikiMacroRenderFlags.TrimTextContent })
public class GWikiColumnMacro extends GWikiHtmlTagMacro implements GWikiBodyEvalMacro
{

  private static final long serialVersionUID = 8874624229089176168L;

  public GWikiColumnMacro()
  {
    setRenderModesFromAnnot();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.append("<td");
    for (String k : attrs.getArgs().getMap().keySet()) {
      ctx.append(" ").append(k).append("=\"").append(StringEscapeUtils.escapeXml(attrs.getArgs().getStringValue(k)))
          .append("\"");
    }

    ctx.append(">\n");
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    ctx.append("\n</td>");
    return true;
  }

}

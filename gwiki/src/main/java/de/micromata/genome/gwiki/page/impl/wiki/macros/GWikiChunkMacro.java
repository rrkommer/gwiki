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
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * Defines a chunk of wiki text inside a wiki page.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "the chunk macro marks a named portion of Wiki text. Optional it displays it as fieldset.",
    params = { @MacroInfoParam(name = "name", required = true, info = "name of the junk"),
        @MacroInfoParam(name = "noDecoration", info = "If set to true, no field set will be used.",
            type = MacroParamType.Boolean) },
    renderFlags = { GWikiMacroRenderFlags.TrimTextContent })
public class GWikiChunkMacro extends GWikiMacroBean implements GWikiBodyEvalMacro
{

  private static final long serialVersionUID = 1687669732947013961L;

  /**
   * used as request parameter and attribute.
   */
  public static final String REQUESTATTR_GWIKICHUNK = "gwikichunk";

  private String name;

  private boolean noDecoration;

  public GWikiChunkMacro()
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
    if (noDecoration == false) {
      ctx.append("<fieldset class=\"gwikichunk\"><legend class=\"gwikichunklegen\">")
          .append(StringEscapeUtils.escapeHtml(name)).append(
              "</legend>");
    }
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    if (noDecoration == false) {
      ctx.append("</fieldset>");
    }
    return true;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public boolean isNoDecoration()
  {
    return noDecoration;
  }

  public void setNoDecoration(boolean noDecoration)
  {
    this.noDecoration = noDecoration;
  }
}

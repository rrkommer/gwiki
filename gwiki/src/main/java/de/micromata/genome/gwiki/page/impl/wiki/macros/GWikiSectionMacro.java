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

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * See also GWikiColumnMacro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "The Macro section can be used in connection with Macro column to define a multiple column layout.",
    params = {
        @MacroInfoParam(name = "width", info = "Optional String, width (px, % or em) of the column."),
        @MacroInfoParam(name = "border", info = "Optional Border attribute of the <table> element"),
        @MacroInfoParam(name = "class", info = "Optional. CSS class attribute of the Column"),
        @MacroInfoParam(name = "style", info = "Optional. CSS style attribute"),
    },
    renderFlags = { GWikiMacroRenderFlags.TrimTextContent, GWikiMacroRenderFlags.NoWrapWithP,
        GWikiMacroRenderFlags.NewLineBlock })
public class GWikiSectionMacro extends GWikiHtmlTagMacro implements GWikiBodyEvalMacro
{

  private static final long serialVersionUID = 6117613653608893766L;

  public GWikiSectionMacro()
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
    ctx.append("<table");
    Map<String, String> params = new HashMap<String, String>();
    params.putAll(attrs.getArgs().getMap());
    if (params.containsKey("width") == false) {
      params.put("width", "100%");
    }
    renderAttributes(ctx, params);
    ctx.append("><tr>");
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    ctx.append("\n</tr></table>");
    return false;
  }

}

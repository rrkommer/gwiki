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

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * Just ignore the macros.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "The Macro anchor defines a local anchor in the current page.<br>"
    + "[#ToLocalAnchor]<br/>\n" +
    "...\n" +
    "{anchor:ToLocalAnchor} Here is the target.",
    params = { @MacroInfoParam(name = "defaultValue", info = "Link to the anchor defined", required = true,
        defaultParameter = true) },
    renderFlags = { GWikiMacroRenderFlags.InTextFlow, GWikiMacroRenderFlags.RteSpan })
public class GWikiLocalAnchorMacroBean extends GWikiMacroBean
{

  private static final long serialVersionUID = -8790099016295725015L;

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return true;
    }
    String localAnchor = attrs.getArgs().getStringValue("defaultValue");
    ctx.append("<a name='", StringEscapeUtils.escapeXml(localAnchor), "'></a>");
    return true;
  }
}

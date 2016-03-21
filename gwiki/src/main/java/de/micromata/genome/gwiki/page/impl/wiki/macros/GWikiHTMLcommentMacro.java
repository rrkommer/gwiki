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

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * Renders nothing (if default hidden is set) or a Html-Comment.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "With the macro HTMLcomment parts of the wiki page can be hidden.",
    params = { @MacroInfoParam(name = "hidden", defaultValue = "true", info = "If true, don't render in html",
        type = MacroParamType.Boolean) })
public class GWikiHTMLcommentMacro extends GWikiMacroBase implements GWikiBodyMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = -5609535631029285273L;

  @Override
  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    boolean hidden = StringUtils.equals(attrs.getDefaultValue(), "hidden");
    if (hidden == true) {
      return true;
    }
    ctx.append("<!-- ");
    ctx.append(attrs.getBody());
    ctx.append("--!>");
    return true;
  }

}

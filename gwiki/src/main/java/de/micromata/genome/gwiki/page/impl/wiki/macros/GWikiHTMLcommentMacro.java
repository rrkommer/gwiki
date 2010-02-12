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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Renders nothing (if default hidden is set) or a Html-Comment.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHTMLcommentMacro extends GWikiMacroBase implements GWikiBodyMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = -5609535631029285273L;

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

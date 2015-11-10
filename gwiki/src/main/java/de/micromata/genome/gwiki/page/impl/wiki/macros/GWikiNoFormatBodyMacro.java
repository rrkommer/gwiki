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
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * GWiki macro to implement the noformat macro.
 * 
 * TODO also as GWikiMacroRte like GWikiCodeMacro
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiNoFormatBodyMacro extends GWikiMacroBase implements GWikiBodyMacro, GWikiRuntimeMacro
{
  private static final long serialVersionUID = 335691916315972801L;

  public GWikiNoFormatBodyMacro()
  {
    setRenderModes(GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NoWrapWithP));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro#render(de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes,
   * de.micromata.genome.gwiki.page.GWikiContext)
   */
  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    ctx.append("<pre style=\"border=1;\">");
    ctx.append(StringEscapeUtils.escapeHtml(attrs.getBody()));
    ctx.append("</pre>");
    return true;
  }

}

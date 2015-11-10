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

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Macro place holder for unknown macro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMacroUnknown implements GWikiRuntimeMacro
{

  private static final long serialVersionUID = -1990609591192712242L;

  private String message;

  public GWikiMacroUnknown()
  {
  }

  public GWikiMacroUnknown(String message)
  {
    this.message = message;
  }

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
  }

  public boolean evalBody()
  {
    return false;
  }

  public boolean hasBody()
  {
    return false;
  }

  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    String msg = message;
    if (msg == null) {
      msg = ctx.getTranslated("gwiki.macro.unknown.error") + StringEscapeUtils.escapeHtml(attrs.getCmd());
    }
    ctx.append("<font color=\"red\">").append(StringEscapeUtils.escapeHtml(msg)).append("</font>");
    return true;
  }

  public int getRenderModes()
  {
    return 0;
  }

}

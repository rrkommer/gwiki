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

package de.micromata.genome.gwiki.page.impl.wiki;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

public abstract class GWikiMacroBase implements GWikiMacro
{
  private int renderModes = 0;

  public boolean hasBody()
  {
    return this instanceof GWikiBodyMacro;
  }

  public boolean evalBody()
  {
    return this instanceof GWikiBodyEvalMacro;
  }

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {

  }

  static public void renderErrorMessage(GWikiContext ctx, String message, MacroAttributes attrs)
  {
    ctx.append("<span style=\"color=red\">").append(StringEscapeUtils.escapeHtml(message)).append("</span>");
  }

  public int getRenderModes()
  {
    return renderModes;
  }

  public void setRenderModes(int renderModes)
  {
    this.renderModes = renderModes;
  }
}

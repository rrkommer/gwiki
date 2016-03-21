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

package de.micromata.genome.gwiki.page.impl.wiki;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

public abstract class GWikiMacroBase implements GWikiMacro
{
  private GWikiMacroInfo macroInfo;
  /**
   * combinations of GWikiMacroRenderFlags
   */
  private int renderModes = 0;

  public GWikiMacroBase(GWikiMacroInfo macroInfo)
  {
    this.macroInfo = macroInfo;
  }

  public GWikiMacroBase()
  {
  }

  @Override
  public boolean hasBody()
  {
    return this instanceof GWikiBodyMacro;
  }

  @Override
  public boolean evalBody()
  {
    return this instanceof GWikiBodyEvalMacro;
  }

  @Override
  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {

  }

  /**
   * For normal wiki errors, use GWikiFragmentError
   * 
   * @param ctx
   * @param message
   * @param attrs
   * @Deprecated
   */

  static public void renderErrorMessage(GWikiContext ctx, String message, MacroAttributes attrs)
  {
    ctx.append("<span style=\"color=red\">").append(StringEscapeUtils.escapeHtml(message)).append("</span>");
  }

  public boolean requirePrepareHeader(GWikiContext ctx)
  {
    return false;
  }

  public void prepareHeader(GWikiContext ctx)
  {

  }

  @Override
  public GWikiMacroInfo getMacroInfo()
  {
    return macroInfo;
  }

  public void setMacroInfo(GWikiMacroInfo macroInfo)
  {
    this.macroInfo = macroInfo;
  }

  @Override
  public int getRenderModes()
  {
    return renderModes;
  }

  public void setRenderModes(int renderModes)
  {
    this.renderModes = renderModes;
  }

  protected void setRenderModesFromAnnot()
  {
    MacroInfo mi = getClass().getAnnotation(MacroInfo.class);
    if (mi == null) {
      return;
    }
    int flags = 0;
    for (GWikiMacroRenderFlags rf : mi.renderFlags()) {
      flags |= rf.getFlag();
    }
    setRenderModes(flags);
  }
}

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

package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * The Class GWikiMacroBase.
 */
public abstract class GWikiMacroBase implements GWikiMacro
{

  /**
   * The macro info.
   */
  private GWikiMacroInfo macroInfo;

  /**
   * combinations of GWikiMacroRenderFlags.
   */
  private int renderModes = 0;

  /**
   * Instantiates a new g wiki macro base.
   *
   * @param macroInfo the macro info
   */
  public GWikiMacroBase(GWikiMacroInfo macroInfo)
  {
    this.macroInfo = macroInfo;
  }

  /**
   * Instantiates a new g wiki macro base.
   */
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
  public boolean isRestricted(MacroAttributes attrs, GWikiContext ctx)
  {
    GWikiAuthorizationRights reqr = requiredRight();
    if (reqr == null) {
      return false;
    }
    return ctx.getWikiWeb().getAuthorization().isAllowTo(ctx, reqr.name()) == false;
  }

  /**
   * Required right.
   *
   * @return the g wiki authorization rights
   */
  protected GWikiAuthorizationRights requiredRight()
  {
    return null;
  }

  @Override
  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
    if (isRestricted(attrs, ctx)) {
      throw new AuthorizationFailedException("Forbitten usage of Macro.");
    }
  }

  /**
   * For normal wiki errors, use GWikiFragmentError.
   *
   * @param ctx the ctx
   * @param message the message
   * @param attrs the attrs
   */

  public static void renderErrorMessage(GWikiContext ctx, String message, MacroAttributes attrs)
  {
    ctx.append("<span style=\"color=red\">").append(WebUtils.escapeHtml(message)).append("</span>");
  }

  /**
   * Require prepare header.
   *
   * @param ctx the ctx
   * @return true, if successful
   */
  public boolean requirePrepareHeader(GWikiContext ctx)
  {
    return false;
  }

  /**
   * Prepare header.
   *
   * @param ctx the ctx
   */
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

  /**
   * Sets the render modes from annot.
   */
  protected void setRenderModesFromAnnot()
  {
    List<MacroInfo> allanots = de.micromata.genome.util.runtime.ClassUtils.findClassAnnotations(getClass(),
        MacroInfo.class);
    for (MacroInfo mi : allanots) {
      if (mi.renderFlags().length > 0) {
        int flags = 0;
        for (GWikiMacroRenderFlags rf : mi.renderFlags()) {
          flags |= rf.getFlag();
        }
        setRenderModes(flags);
        return;
      }
    }

  }
}

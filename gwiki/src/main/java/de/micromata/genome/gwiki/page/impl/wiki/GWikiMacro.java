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

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Macros will be created for each instanceof of a macro node in the document.
 * 
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiMacro
{
  /**
   * 
   * @return true if the macro expexts a body.
   */
  public boolean hasBody();

  /**
   * 
   * @return true, if the body is not plain text, but itself should be parsed as wiki text.
   */
  public boolean evalBody();

  /**
   * Pages with this macro cannot be saved by current user.
   *
   * @param attrs the attrs
   * @param ctx the ctx
   * @return true, if is restricted
   */
  public boolean isRestricted(MacroAttributes attrs, GWikiContext ctx);

  /**
   * Will be called if a wiki artefakt will be safed by the user. The implementation should throw
   * AuthorizationFailedException if the current user has not the right to make usage of this macro or use invalid
   * attributes.
   * 
   * @param attrs the attributes of the macro.
   * @param ctx Context.
   * @throws AuthorizationFailedException
   */
  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException;

  /**
   * combination of GWikiMacroRenderFlags
   * 
   * @return
   */
  public int getRenderModes();

  public GWikiMacroInfo getMacroInfo();

}

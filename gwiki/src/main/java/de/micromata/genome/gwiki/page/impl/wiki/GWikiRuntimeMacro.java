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

import java.io.Serializable;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Macro will evaluated at runtime.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiRuntimeMacro extends GWikiMacro, Serializable
{
  /**
   * render method.
   * 
   * @param attrs Macro attributes.
   * @param ctx Context.
   * @return true if page should be evaluated after macro processing.
   */
  public boolean render(MacroAttributes attrs, final GWikiContext ctx);

}

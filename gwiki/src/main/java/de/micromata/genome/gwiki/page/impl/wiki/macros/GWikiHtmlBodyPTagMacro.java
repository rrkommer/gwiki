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

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;

/**
 * Like GWikiHtmlBodyTagMacro, but the macro generates a html, which does not allow Wrapped with p.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlBodyPTagMacro extends GWikiHtmlBodyTagMacro
{

  /**
   * 
   */
  private static final long serialVersionUID = 6120276764387653968L;

  public GWikiHtmlBodyPTagMacro()
  {
    super();
    setRenderModes(GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart, GWikiMacroRenderFlags.NewLineBeforeEnd,
        GWikiMacroRenderFlags.NoWrapWithP));
  }

}

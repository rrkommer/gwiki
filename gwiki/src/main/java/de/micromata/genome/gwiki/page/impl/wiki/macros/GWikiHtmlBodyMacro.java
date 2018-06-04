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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentUnsecureHtml;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;

/**
 * Macro to implement the html macro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlBodyMacro extends GWikiCompileTimeMacroBase implements GWikiBodyMacro, GWikiCompileTimeMacro
{

  private static final long serialVersionUID = -2785686179610004641L;

  @Override
  protected GWikiAuthorizationRights requiredRight()
  {
    return GWikiAuthorizationRights.GWIKI_EDITHTML;
  }

  @Override
  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
    if (isRestricted(attrs, ctx)) {
      throw new AuthorizationFailedException("Unsecure usage of HTML Macro.");
    }
  }

  @Override
  public Collection<GWikiFragment> getFragments(GWikiMacroFragment macroFrag, GWikiWikiTokens tks,
      GWikiWikiParserContext ctx)
  {
    Collection<GWikiFragment> frags = new ArrayList<GWikiFragment>();
    macroFrag.addChild(new GWikiFragmentUnsecureHtml(StringUtils.defaultString(macroFrag.getAttrs().getBody())));
    frags.add(macroFrag);
    return frags;
  }
}

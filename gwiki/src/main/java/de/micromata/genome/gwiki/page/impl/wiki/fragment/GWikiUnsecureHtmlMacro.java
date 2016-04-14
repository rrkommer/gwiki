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

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;

@MacroInfo(info = "The macro html marks a region of text as pure HTML.<br/>"
    + "To save a page containing the html macro the user requires the right GWIKI_EDITHTML.",
    renderFlags = { GWikiMacroRenderFlags.NewLineBlock,
        GWikiMacroRenderFlags.NoWrapWithP, GWikiMacroRenderFlags.ContainsTextBlock,
        GWikiMacroRenderFlags.TrimTextContent })
public class GWikiUnsecureHtmlMacro extends GWikiCompileTimeMacroBase implements GWikiCompileTimeMacro
{

  private static final long serialVersionUID = 6651596226823444417L;

  public GWikiUnsecureHtmlMacro(GWikiMacroInfo macroInfo)
  {
    super(macroInfo);
    setRenderModesFromAnnot();
  }

  @Override
  protected GWikiAuthorizationRights requiredRight()
  {
    return GWikiAuthorizationRights.GWIKI_EDITHTML;
  }

  @Override
  public boolean evalBody()
  {
    return false;
  }

  @Override
  public boolean hasBody()
  {
    return true;
  }

  @Override
  public Collection<GWikiFragment> getFragments(GWikiMacroFragment macroFrag, GWikiWikiTokens tks,
      GWikiWikiParserContext ctx)
  {
    List<GWikiFragment> fragl = new ArrayList<GWikiFragment>();
    macroFrag.addChild(new GWikiFragmentUnsecureHtml(macroFrag.getAttrs().getBody()));
    fragl.add(macroFrag);
    return fragl;
  }

}
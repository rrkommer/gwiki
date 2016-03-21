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

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroSourceable;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;
import de.micromata.genome.gwiki.utils.html.SaxElementMatchers;

/**
 * Render a Quote section.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "The macro quote marks a section of wiki text as quoted text.",
    renderFlags = { GWikiMacroRenderFlags.TrimTextContent, GWikiMacroRenderFlags.NoWrapWithP })
public class GWikiQuoteMacroBean extends GWikiMacroBean
    implements GWikiBodyEvalMacro, GWikiMacroRte, GWikiMacroSourceable
{

  private static final long serialVersionUID = -3030397042733595461L;

  private static Html2WikiTransformInfo transformInfo = new Html2WikiTransformInfo("blockquote",
      SaxElementMatchers.nameMatcher("blockquote"), "quote", GWikiQuoteMacroBean.class);

  public GWikiQuoteMacroBean()
  {
    setRenderModesFromAnnot();
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.append("<blockquote>");
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    ctx.append("</blockquote>");
    return true;
  }

  @Override
  public Html2WikiTransformInfo getTransformInfo()
  {
    return transformInfo;
  }

  @Override
  public boolean evalBody()
  {
    return true;
  }

  @Override
  public void toSource(GWikiMacroFragment macroFragment, StringBuilder sb)
  {
    if (macroFragment.getAttrs().getArgs().isEmpty() == true) {
      macroFragment.getAttrs().getArgs().setBooleanValue("start", true);
    }
    macroFragment.getMacroSource(sb);

  }

}

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

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;
import de.micromata.genome.gwiki.utils.html.SaxElementMatchers;
import de.micromata.genome.util.matcher.CommonMatchers;
import de.micromata.genome.util.matcher.StringMatchers;

/**
 * Render a pageIntro section.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(
    info = "The Macro pageintro markes a section of wiki text, which will be used as short abstract of the page.\n" +
        "This text can also be used to be displayed in children Macros.",
    renderFlags = { GWikiMacroRenderFlags.NewLineAfterStart, GWikiMacroRenderFlags.NewLineBeforeEnd,
        GWikiMacroRenderFlags.NoWrapWithP, GWikiMacroRenderFlags.ContainsTextBlock,
        GWikiMacroRenderFlags.TrimTextContent })
public class GWikiPageIntroMacroBean extends GWikiMacroBean implements GWikiBodyEvalMacro//, GWikiMacroRte
{

  private static final long serialVersionUID = -3030397042733595461L;

  private static Html2WikiTransformInfo transformInfo = new Html2WikiTransformInfo("div",
      CommonMatchers.and(SaxElementMatchers.nameMatcher("div"),
          SaxElementMatchers.attribute("class", StringMatchers.containsString("wikiPageIntro"))),
      "pageintro",
      GWikiPageIntroMacroBean.class)
  {

    @Override
    public void handleMacroEnd(String tagname, GWikiMacroFragment lpfm, List<GWikiFragment> children, String body)
    {
      List<GWikiFragment> nc = new ArrayList<GWikiFragment>(children);
      if (nc.isEmpty() == false && nc.get(nc.size() - 1) instanceof GWikiFragmentP) {
        nc.remove(nc.size() - 1);
      }
      super.handleMacroEnd(tagname, lpfm, nc, body);
    }

  };

  public GWikiPageIntroMacroBean()
  {
    setRenderModesFromAnnot();
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.append("<div class=\"wikiPageIntro");
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == true) {
      ctx.append(" weditmacrohead");
    }
    ctx.append("\"");
    Html2WikiTransformInfo.renderMacroArgs(ctx, attrs);
    ctx.append(">");
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == true) {
      ctx.append("<div class='weditmacrobody'>");
    }
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == true) {
      ctx.append("</div>");
    }
    ctx.append("</div>");
    return true;
  }

  //  @Override
  public Html2WikiTransformInfo getTransformInfo()
  {
    return transformInfo;
  }

  @Override
  public boolean evalBody()
  {
    return true;
  }

}

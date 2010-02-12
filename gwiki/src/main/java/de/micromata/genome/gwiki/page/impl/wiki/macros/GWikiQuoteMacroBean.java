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

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;

/**
 * Render a Quote section.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiQuoteMacroBean extends GWikiMacroBean implements GWikiBodyEvalMacro, GWikiMacroRte
{

  private static final long serialVersionUID = -3030397042733595461L;

  private static Html2WikiTransformInfo transformInfo = new Html2WikiTransformInfo("blockquote", "quote", GWikiQuoteMacroBean.class);
  static {
    // AttributeMatcher am = new AttributeMatcher();
    // am.setName("class");
    // am.setValueMatcher(new EqualsMatcher<String>("wikiPageIntro"));
    // transformInfo.getAttributeMatcher().add(am);
  }

  public GWikiQuoteMacroBean()
  {
    setRenderModes(GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart, GWikiMacroRenderFlags.NewLineBeforeEnd));
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

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
package de.micromata.genome.gwiki.page.impl.wiki.slideshow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSlideShowMacro extends GWikiCompileTimeMacroBase implements GWikiCompileTimeMacro, GWikiRuntimeMacro, GWikiBodyEvalMacro
{

  private static final long serialVersionUID = 3025588244594913253L;

  public static String GWIKI_SLIDESHOW_SECTION = "GWIKI_SLIDESHOW_SECTION";

  public GWikiSlideShowMacro()
  {
    setRenderModes(GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.TrimTextContent, GWikiMacroRenderFlags.NoWrapWithP));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro#getFragments(de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment
   * , de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens,
   * de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext)
   */
  public Collection<GWikiFragment> getFragments(GWikiMacroFragment macroFrag, GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    // ctx.getMacroFactories().put(
    // "slides",
    // new GWikiMacroClassFactory(GWikiSlidesMacro.class, GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.TrimTextContent,
    // GWikiMacroRenderFlags.NoWrapWithP)));
    ctx.getMacroFactories().put("slide",
        new GWikiMacroClassFactory(GWikiSlideMacro.class, GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.TrimTextContent)));
    ctx.getMacroFactories().put("slidefooter",
        new GWikiMacroClassFactory(GWikiSlideFooterMacro.class, GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.TrimTextContent)));
    ctx.getMacroFactories().put("slideheader",
        new GWikiMacroClassFactory(GWikiSlideHeaderMacro.class, GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.TrimTextContent)));
    ctx.getMacroFactories().put("incremental", new GWikiMacroClassFactory(GWikiSlideIncrementalMacro.class));
    ctx.getMacroFactories().put("slidestyle", new GWikiMacroClassFactory(GWikiSlideStyleMacro.class));
    ctx.getMacroFactories().put("slidehandout", new GWikiMacroClassFactory(GWikiSlideHandoutMacro.class));
    List<GWikiFragment> ret = new ArrayList<GWikiFragment>(1);
    ret.add(macroFrag);
    return ret;
    // return Collections.emptyList();
  }

  @Override
  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {

    boolean asSlide = "true".equals(ctx.getRequestParameter("asSlide"));
    if (asSlide == false) {
      attrs.getChildFragment().render(ctx);
      return true;
    }
    GWikiFragmentChildContainer cc = attrs.getChildFragment();
    for (GWikiFragment cf : cc.getChilds()) {
      if (cf instanceof GWikiMacroFragment) {
        GWikiMacroFragment mf = (GWikiMacroFragment) cf;
        String cmd = mf.getAttrs().getCmd();
        String slideSection = (String) ctx.getRequestAttribute(GWIKI_SLIDESHOW_SECTION);
        if (StringUtils.equals(slideSection, "layout") == true) {
          if ("slideheader".equals(cmd) == true || "slidefooter".equals(cmd) == true) {
            cf.render(ctx);
          }
        } else if (StringUtils.equals(slideSection, "slides") == true) {
          if ("slide".equals(cmd) == true) {
            cf.render(ctx);
          }
        }
      }
    }
    return true;
  }
}

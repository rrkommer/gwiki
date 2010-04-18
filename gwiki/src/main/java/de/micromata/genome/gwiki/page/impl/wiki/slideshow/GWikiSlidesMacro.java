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

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@Deprecated
public class GWikiSlidesMacro extends GWikiMacroBean implements GWikiBodyEvalMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = -7833799251865329225L;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
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
        if ("slide".equals(cmd) == true) {
          cf.render(ctx);
        } else {
          ctx.append("<div class=\"handout\">");
          cf.render(ctx);
          ctx.append("</div>");

        }
      }
    }
    return true;
  }

}

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

package de.micromata.genome.gwiki.page.impl.wiki.blueprint;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroSourceable;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "generates a formLabel",
    renderFlags = { GWikiMacroRenderFlags.TrimTextContent, GWikiMacroRenderFlags.ContainsTextBlock,
        GWikiMacroRenderFlags.NoWrapWithP })
public class GWikiFormLabelMacro extends GWikiMacroBean implements GWikiBodyEvalMacro, GWikiMacroSourceable
{

  private static final long serialVersionUID = 8655893189760899090L;

  public GWikiFormLabelMacro()
  {
    setRenderModesFromAnnot();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroSourceable#toSource(de.micromata.genome.gwiki.page.impl.wiki.
   * GWikiMacroFragment, java.lang.StringBuilder)
   */
  @Override
  public void toSource(GWikiMacroFragment macroFragment, StringBuilder sb, GWikiFragment parent, GWikiFragment previous,
      GWikiFragment next)
  {
    if (GWikiFormMacro.evalForm() == false) {
      macroFragment.getSource(sb, parent, previous, next);
    }
  }

  @Override
  protected GWikiAuthorizationRights requiredRight()
  {
    return GWikiAuthorizationRights.GWIKI_EDITHTML;
  }

}

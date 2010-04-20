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

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiWithHeaderPrepare;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * For slide shows will be inserted into the head sections.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSlideStyleMacro extends GWikiMacroBean implements GWikiRuntimeMacro, GWikiWithHeaderPrepare, GWikiBodyMacro
{

  private static final long serialVersionUID = -815738777375318294L;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    // nothing here
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiWithHeaderPrepare#prepareHeader(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void prepareHeader(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.getRequiredHeader().add(attrs.getBody());
  }

  @Override
  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
    super.ensureRight(attrs, ctx);
    ctx.ensureAllowTo(GWikiAuthorizationRights.GWIKI_EDITHTML.name());
  }
}

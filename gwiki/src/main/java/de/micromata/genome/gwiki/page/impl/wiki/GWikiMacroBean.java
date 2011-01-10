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

package de.micromata.genome.gwiki.page.impl.wiki;

import org.apache.commons.beanutils.BeanUtilsBean;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Simple render Bean. MacroAttributes will be mapped to Bean properties.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiMacroBean extends GWikiMacroBase implements GWikiRuntimeMacro
{

  private static final long serialVersionUID = 317825823838434281L;

  // protected transient GWikiContext wikiContext;

  private boolean populated = false;

  public GWikiMacroBean()
  {

  }

  public void populateIfNeeded(MacroAttributes attrs, GWikiContext ctx)
  {
    if (populated == true) {
      return;
    }
    populate(attrs, ctx);
    populated = true;
  }

  protected void populate(MacroAttributes attrs, GWikiContext ctx)
  {
    try {
      BeanUtilsBean.getInstance().populate(this, attrs.getArgs().getMap());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

  }

  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    if (populated == false) {
      populated = true;
      populate(attrs, ctx);
    }
    return renderImpl(ctx, attrs);

  }

  public abstract boolean renderImpl(GWikiContext ctx, MacroAttributes attrs);

}

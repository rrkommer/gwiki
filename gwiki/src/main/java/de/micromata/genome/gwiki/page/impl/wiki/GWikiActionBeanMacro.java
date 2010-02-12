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
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBean;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanUtils;

/**
 * Adapter from Macro to a ActionBean.
 * 
 * First the macroAttributes will be mapped to bean and than the request parameters with prefixed names.
 * 
 * never used.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiActionBeanMacro extends GWikiMacroBase implements GWikiRuntimeMacro, ActionBean
{

  private static final long serialVersionUID = 727126974787360910L;

  protected transient GWikiContext wikiContext;

  protected MacroAttributes macroAttributes;

  // TODO gwiki ActionBeanMacros has may be bodies?
  public boolean evalBody()
  {
    return false;
  }

  public boolean hasBody()
  {
    return false;
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
    this.macroAttributes = attrs;
    this.wikiContext = ctx;
    populate(attrs, ctx);
    return ActionBeanUtils.perform(this);
  }

  public String getRequestPrefix()
  {
    return macroAttributes.getRequestPrefix();
  }

  public GWikiContext getWikiContext()
  {
    return wikiContext;
  }

  public void setWikiContext(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

}

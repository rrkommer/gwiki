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
package de.micromata.genome.gwiki.page.impl.wiki.blueprint;

import java.util.Collections;
import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@Deprecated
public class GWikiFragmentFormElement extends GWikiFragmentBase
{

  private static final long serialVersionUID = 7867252933757078447L;

  public static final String EVAL_FORM = GWikiFragmentFormElement.class.getName() + ".EVAL_FORM";

  private String varName;

  public GWikiFragmentFormElement()
  {

  }

  public GWikiFragmentFormElement(String varName)
  {
    super();
    this.varName = varName;
  }

  protected boolean getFormSource(StringBuilder sb)
  {
    if (GWikiContext.getCurrent() == null) {
      return false;
    }
    if (GWikiContext.getCurrent().getRequestAttribute(EVAL_FORM) != Boolean.TRUE) {
      return false;
    }
    String v = GWikiContext.getCurrent().getRequestParameter(varName);
    if (v == null) {
      return false;
    }
    sb.append(v);
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragementBase#getSource(java.lang.StringBuilder)
   */
  @Override
  public void getSource(StringBuilder sb)
  {
    if (getFormSource(sb) == false) {
      sb.append("@").append(varName).append("@");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment#ensureRight(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment#getChilds()
   */
  public List<GWikiFragment> getChilds()
  {
    return Collections.emptyList();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment#render(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public boolean render(GWikiContext ctx)
  {
    ctx.append("<input type=\"text\" name=\"" + varName + "\"/>");
    return true;
  }

  public String getVarName()
  {
    return varName;
  }

  public void setVarName(String varName)
  {
    this.varName = varName;
  }

}

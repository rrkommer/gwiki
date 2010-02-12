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

import java.io.Serializable;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiScriptMacro;

public class GWikiScriptMacroFactory implements GWikiMacroFactory, GWikiPropKeys, Serializable
{

  private static final long serialVersionUID = 396117543440444247L;

  private GWikiElementInfo elementInfo;

  public GWikiScriptMacroFactory(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }
  public String toString()
  {
    return "GWikiScriptMacro(" + elementInfo.getId() + ")"; 
    
  }
  public GWikiMacro createInstance()
  {
    GWikiScriptMacro ma = new GWikiScriptMacro(this, GWikiWeb.get().getElement(elementInfo));
    return ma;
  }

  public boolean evalBody()
  {
    return elementInfo.getProps().getBooleanValue(MACRO_EVAL_BODY);
  }

  public boolean hasBody()
  {
    return elementInfo.getProps().getBooleanValue(MACRO_WITH_BODY);
  }

  public boolean isRteMacro()
  {
    return false;
  }

  public GWikiElementInfo getElement()
  {
    return elementInfo;
  }

  public void setElement(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

}

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
package de.micromata.genome.gwiki.pagetemplates_1_0.wizard;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * @author stefans
 *
 */
public abstract class AbstractStepWizardAction extends ActionBeanBase
{
  private GWikiElement element;
  
  public Object onRenderHeader() {
    GWikiElement actionPage = wikiContext.getCurrentElement();
    GWikiElementInfo info = actionPage.getElementInfo();
    String tabTitle = info.getProps().getStringValue(GWikiPropKeys.TITLE);

    if (tabTitle.startsWith("I{") == true) {
      tabTitle = wikiContext.getTranslatedProp(tabTitle);
    }
    String divAnchor = StringUtils.substringAfterLast(info.getId(), "/");
    wikiContext.append("<li><a href='#").append(divAnchor).append("'>").append(tabTitle).append("</a></li>");
    return noForward();
  }
  
  /**
   * @param element the element to set
   */
  public void setElement(GWikiElement element)
  {
    this.element = element;
  }

  /**
   * @return the element
   */
  public GWikiElement getElement()
  {
    return element;
  }

}

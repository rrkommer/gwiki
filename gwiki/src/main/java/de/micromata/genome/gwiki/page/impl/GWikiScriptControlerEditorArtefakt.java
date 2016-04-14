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

package de.micromata.genome.gwiki.page.impl;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * A editor artefakt for groovy controler action beans.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiScriptControlerEditorArtefakt extends GWikiCodePageEditorArtefakt
{
  private static final long serialVersionUID = -4234425256998011853L;

  private GWikiScriptControlerArtefakt groovyPage;

  public GWikiScriptControlerEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiScriptControlerArtefakt textPage)
  {
    super(elementToEdit, editBean, partName, textPage);
    this.groovyPage = textPage;
  }

  @Override
  public void onSave(GWikiContext ctx)
  {
    super.onSave(ctx);
    groovyPage.setCompiledObject(null);
    if (ctx.hasValidationErrors() == true)
      return;
    try {
      groovyPage.getActionBeanClass(ctx);
    } catch (Throwable ex) {
      ctx.addSimpleValidationError("Failure to compile Groovy Action: " + GWikiJspPageEditorArtefakt.getCompileError(ex));
    }
  }

  @Override
  protected String getCodeType()
  {
    return "java";
  }
}
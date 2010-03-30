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

package de.micromata.genome.gwiki.page.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Editor for a GSPT/Jsp page.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiJspPageEditorArtefakt extends GWikiCodePageEditorArtefakt
{

  private static final long serialVersionUID = 433557872072235804L;

  private GWikiJspTemplateArtefakt jspPage;

  public GWikiJspPageEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiJspTemplateArtefakt textPage)
  {
    super(elementToEdit, editBean, partName, textPage);
    this.jspPage = textPage;
  }

  public static String getCompileError(Throwable ex)
  {
    StringWriter sout = new StringWriter();
    sout.append(ex.getMessage() + "\n");
    PrintWriter pout = new PrintWriter(sout);
    ex.printStackTrace(pout);
    return sout.getBuffer().toString();
  }

  @Override
  public void onSave(GWikiContext ctx)
  {
    super.onSave(ctx);
    jspPage.setCompiledObject(null);
    if (ctx.hasValidationErrors() == true)
      return;
    try {
      jspPage.compile(ctx);
    } catch (Throwable ex) {
      ctx.addValidationError("gwiki.artefakt.jsppage.message.compileerror", getCompileError(ex));
    }
  }

  @Override
  protected String getCodeType()
  {
    return "html";
  }

}

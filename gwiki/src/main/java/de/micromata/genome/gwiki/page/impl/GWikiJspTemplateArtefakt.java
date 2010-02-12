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

import java.io.Serializable;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Artefact for a Jsp (GSPT) artefact.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiJspTemplateArtefakt extends GWikiTextArtefaktBase<Serializable> implements GWikiExecutableArtefakt<Serializable>,
    GWikiEditableArtefakt
{

  private static final long serialVersionUID = -699812098888363361L;

  public String getFileSuffix()
  {
    return ".gspt";
  }

  protected Serializable compile(GWikiContext ctx)
  {
    if (getCompiledObject() != null)
      return getCompiledObject();
    Serializable s = ctx.getWikiWeb().getJspProcessor().compile(ctx, getStorageData());
    setCompiledObject(s);
    return s;
  }

  public boolean renderWithParts(final GWikiContext ctx)
  {
    Serializable template = compile(ctx);
    ctx.getWikiWeb().getJspProcessor().renderTemplate(ctx, template);
    return true;
  }

  public GWikiEditorArtefakt getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiJspPageEditorArtefakt(elementToEdit, bean, partKey, this);
  }

}

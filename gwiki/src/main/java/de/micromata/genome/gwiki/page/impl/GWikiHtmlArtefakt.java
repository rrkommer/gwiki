////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.GWikiIndexedArtefakt;
import de.micromata.genome.gwiki.utils.AppendableI;

/**
 * Artefakt for HTML content.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlArtefakt extends GWikiTextArtefaktBase<String> implements GWikiExecutableArtefakt<String>, GWikiIndexedArtefakt,
    GWikiEditableArtefakt
{
  private static final long serialVersionUID = 8904411739515655880L;

  public String getFileSuffix()
  {
    return ".html";
  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    ctx.append(getStorageData());
    return true;

  }

  public void getPreview(GWikiContext ctx, AppendableI sb)
  {
    sb.append(getStorageData());
  }

  public GWikiEditorArtefakt< ? > getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiHtmlEditorArtefakt(elementToEdit, bean, partKey, this);
  }

}

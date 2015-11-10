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

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiArtefaktBase;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Base implementation for GWikiEditorArtefakt.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 * @param <T>
 */
public abstract class GWikiEditorArtefaktBase<T extends Serializable> extends GWikiArtefaktBase<T> implements GWikiEditorArtefakt<T>
{

  private static final long serialVersionUID = -5010610329543479810L;

  protected String partName;

  protected GWikiElement elementToEdit;

  protected GWikiEditPageActionBean editBean;

  public GWikiEditorArtefaktBase(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName)
  {
    this.partName = partName;
    this.elementToEdit = elementToEdit;
    this.editBean = editBean;
  }
  
  @Override
  public void prepareHeader(GWikiContext wikiContext)
  {
    super.prepareHeader(wikiContext);
  }

  public String getTabTitle()
  {
    return StringUtils.defaultString(partName);
  }

  public String getPartName()
  {
    return partName;
  }

  public void setPartName(String partName)
  {
    this.partName = partName;
  }

  public GWikiElement getElementToEdit()
  {
    return elementToEdit;
  }

  public void setElementToEdit(GWikiElement elementToEdit)
  {
    this.elementToEdit = elementToEdit;
  }

  public GWikiEditPageActionBean getEditBean()
  {
    return editBean;
  }

  public void setEditBean(GWikiEditPageActionBean editBean)
  {
    this.editBean = editBean;
  }
}

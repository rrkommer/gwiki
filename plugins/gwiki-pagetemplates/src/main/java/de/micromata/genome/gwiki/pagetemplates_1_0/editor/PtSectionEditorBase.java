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
package de.micromata.genome.gwiki.pagetemplates_1_0.editor;

import java.io.Serializable;

import de.micromata.genome.gwiki.model.GWikiArtefaktBase;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class PtSectionEditorBase<T extends Serializable> extends GWikiArtefaktBase<T> implements GWikiEditorArtefakt<T>
{
  private static final long serialVersionUID = 7611579351674072940L;

  protected GWikiElement element;

  protected String sectionName;

  protected String editor;
  
  
  public PtSectionEditorBase(GWikiElement element, String sectionName, String editor)
  {
    this.element = element;
    this.sectionName = sectionName;
    this.editor = editor;
  }
}

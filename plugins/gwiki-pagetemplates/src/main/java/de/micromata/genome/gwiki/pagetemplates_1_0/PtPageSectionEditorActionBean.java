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
package de.micromata.genome.gwiki.pagetemplates_1_0;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PtPageSectionEditorActionBean extends ActionBeanBase
{
  private String pageId;

  private String sectionName;

  private String editor;

  private GWikiEditorArtefakt< ? > secEditor;

  private GWikiElement element;

  private GWikiEditorArtefakt< ? > createEditor()
  {
    element = wikiContext.getWikiWeb().getElement(pageId);
    return new PtWikiRawTextEditor(element, sectionName, editor);
  }

  private void init()
  {
    secEditor = createEditor();
  }

  public Object onInit()
  {
    init();
    return null;
  }

  public Object onSave()
  {
    init();
    secEditor.onSave(wikiContext);

    if (wikiContext.hasValidationErrors() == false) {
      return pageId;
    }
    wikiContext.getWikiWeb().getStorage().storeElement(wikiContext, element, false);
    return null;
  }

  public Object onCancel()
  {
    return pageId;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getSectionName()
  {
    return sectionName;
  }

  public void setSectionName(String sectionName)
  {
    this.sectionName = sectionName;
  }

  public String getEditor()
  {
    return editor;
  }

  public void setEditor(String editor)
  {
    this.editor = editor;
  }

  public GWikiEditorArtefakt< ? > getSecEditor()
  {
    return secEditor;
  }

  public void setSecEditor(GWikiEditorArtefakt< ? > secEditor)
  {
    this.secEditor = secEditor;
  }

}

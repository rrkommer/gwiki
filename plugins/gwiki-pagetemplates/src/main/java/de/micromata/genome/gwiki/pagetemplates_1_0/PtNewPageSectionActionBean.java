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

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.pagetemplates_1_0.macro.PtSectionMacroBean;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * @see PtSectionMacroBean
 * @deprecated probably to be removed.
 */
public class PtNewPageSectionActionBean extends ActionBeanBase
{
  private String state = "TITLE";

  private String pageId;

  private String sectionName;

  private String editor;

  private GWikiElementInfo eli = new GWikiElementInfo(new GWikiSettingsProps(), null);

  public Object onInit()
  {
    return null;
  }

  public Object onSafe()
  {
    return null;
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

  public GWikiElementInfo getEli()
  {
    return eli;
  }

  public void setEli(GWikiElementInfo eli)
  {
    this.eli = eli;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }
}

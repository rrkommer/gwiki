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
package de.micromata.genome.gwiki.model.filter;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Filter will be called while rendering gui to have the posibility to render additionally markup.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSkinRenderFilterEvent extends GWikiElementFilterEvent
{
  /**
   * Gui placements to include:
   * 
   * @author Roger Rene Kommer (r.kommer@micromata.de)
   * 
   */
  public static enum GuiElementType
  {
    BeginBody, //
    EndBody, //
    HeadMenu, //
    PageMenu, //
    ;
  }

  private String guiElementType;

  /**
   * @param wikiContext
   * @param element
   */
  public GWikiSkinRenderFilterEvent(GWikiContext wikiContext, GWikiElement element, String guiElementType)
  {
    super(wikiContext, element);
    this.guiElementType = guiElementType;
  }

  public String getGuiElementType()
  {
    return guiElementType;
  }

  public void setGuiElementType(String guiElementType)
  {
    this.guiElementType = guiElementType;
  }

}

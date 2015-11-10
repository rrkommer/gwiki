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
package de.micromata.genome.gwiki.sampleplugin_1_0;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilter;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilterEvent;
import de.micromata.genome.gwiki.model.filter.GWikiSkinRenderFilterEvent.GuiElementType;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SampleSkinRenderFilter implements GWikiSkinRenderFilter
{

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiSkinRenderFilterEvent, GWikiSkinRenderFilter> chain, GWikiSkinRenderFilterEvent event)
  {
    if (StringUtils.equals(event.getWikiContext().getRequestParameter("showSampleSkinRenderFilter"), "true") == false) {
      return chain.nextFilter(event);
    }
    if (GuiElementType.BeginBody.name().equals(event.getGuiElementType()) == true) {
      event.getWikiContext().append("Here starts Body");
    } else if (GuiElementType.HeadMenu.name().equals(event.getGuiElementType()) == true) {
      event.getWikiContext().append("Menu item");
    } else {
      event.getWikiContext().append("Gui Element Type: " + event.getGuiElementType());
    }
    return chain.nextFilter(event);
  }

}

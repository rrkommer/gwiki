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

package de.micromata.genome.gwiki.plugin.wikilink_1_0;

import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageCompileFilter;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageCompileFilterEvent;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageBaseArtefakt;

/**
 * Filters Wiki content and inserts traditional wiki links automatically.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiLinkFilter implements GWikiWikiPageCompileFilter
{
  public Void filter(GWikiFilterChain<Void, GWikiWikiPageCompileFilterEvent, GWikiWikiPageCompileFilter> chain,
      GWikiWikiPageCompileFilterEvent event)
  {
    chain.nextFilter(event);
    int renderMode = event.getWikiContext().getRenderMode();
    if (RenderModes.NoLinks.isSet(renderMode) == true
        || RenderModes.ForText.isSet(renderMode) == true
        || RenderModes.ForRichTextEdit.isSet(renderMode) == true) {
      return null;
    }
    if (new GWikiWikiLinkConfig(event.getWikiContext()).isWikiPageEnabled(event.getElement()) == false) {
      return null;
    }
    GWikiWikiPageBaseArtefakt a = event.getWikiPageArtefakt();
    GWikiContent content = a.getCompiledObject();

    if (content != null) {
      if (event.getElement() == null) {
        return null;
      }
      content.iterate(new GWikiWikiLinkContentIterator());
    }
    return null;
  }

}

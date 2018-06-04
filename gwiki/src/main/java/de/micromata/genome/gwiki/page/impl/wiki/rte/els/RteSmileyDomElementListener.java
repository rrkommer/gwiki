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

package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiFragmentSmiley;
import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiSmileyConfig;
import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiSmileyInfo;

public class RteSmileyDomElementListener implements DomElementListener
{
  GWikiSmileyConfig smileyConfig = null;

  @Override
  public boolean listen(DomElementEvent event)
  {
    if (smileyConfig == null) {
      smileyConfig = GWikiSmileyConfig.get(event.getWikiContext());
    }
    String smilyId = event.getAttr("data-wiki-smiley");
    if (StringUtils.isBlank(smilyId) == true) {
      return true;
    }
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.flushText();
    event.walker.skipChildren();
    GWikiSmileyInfo smi = smileyConfig.getSmileysByName().get(smilyId);
    if (smi != null) {
      parseContext.addFragment(new GWikiFragmentSmiley(smi));
    }
    return false;
  }

  @Override
  public int getPrio()
  {
    return 20;
  }

}

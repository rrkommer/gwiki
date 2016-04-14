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

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBr;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class RteBrDomElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    event.getParseContext().flushText();
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.addFragment(getNlFragement(parseContext, new GWikiFragmentBr()));
    return false;
  }

  @SuppressWarnings("unchecked")
  public boolean needSoftNl(GWikiWikiParserContext parseContext)
  {
    //return parseContext.findFragsInStack(GWikiFragmentLi.class, GWikiFragmentTable.class) != null;
    return false;
  }

  public GWikiFragment getNlFragement(GWikiWikiParserContext parseContext, GWikiFragment defaultFrag)
  {
    //    if (needSoftNl(parseContext) == true) {
    return new GWikiFragmentBr();
    //    }
    //    return defaultFrag;
  }
}

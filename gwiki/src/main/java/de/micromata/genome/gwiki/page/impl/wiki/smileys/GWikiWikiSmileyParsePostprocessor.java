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

package de.micromata.genome.gwiki.page.impl.wiki.smileys;

import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParsePostprocessor;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;

public class GWikiWikiSmileyParsePostprocessor implements GWikiWikiParsePostprocessor
{
  @Override
  public void process(GWikiWikiParserContext ctx)
  {
    GWikiContext wikiContext = GWikiContext.getCurrent();
    if (wikiContext == null) {
      return;
    }

    List<GWikiFragment> frags = ctx.popFragList();
    GWikiFragmentChildContainer chcont = new GWikiFragmentChildContainer(frags);

    chcont.iterate(new GWikiSmileyContentIterator(wikiContext), null);
    List<GWikiFragment> nfrags = chcont.getChilds();
    ctx.pushFragList(nfrags);
  }

  @Override
  public int getPrio()
  {
    return 200;
  }
}

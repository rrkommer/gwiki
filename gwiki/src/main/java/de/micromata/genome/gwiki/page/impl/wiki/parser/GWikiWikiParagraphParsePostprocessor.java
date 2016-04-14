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

package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserUtils.RevisePsVisitor;

public class GWikiWikiParagraphParsePostprocessor implements GWikiWikiParsePostprocessor
{

  @Override
  public void process(GWikiWikiParserContext ctx)
  {
    if (GWikiWikiParser.isPAllowedInDom(ctx) == false) {
      return;
    }
    List<GWikiFragment> l = ctx.popFragList();
    if (l.isEmpty() == true) {
      ctx.pushFragList(l);
      return;
    }
    l = GWikiWikiParser.addWrappedP(l);
    ctx.pushFragList(l);

    List<GWikiFragment> frags = ctx.popFragList();
    GWikiFragmentChildContainer chcont = new GWikiFragmentChildContainer(frags);
    //    StringBuilder sb = new StringBuilder();
    //    WikiParserUtils.dumpFragmentTree(chcont, sb, "");
    //    System.out.println("Beforetrans: " + sb.toString());
    //    sb.setLength(0);
    RevisePsVisitor visit = new RevisePsVisitor();
    chcont.iterate(visit, null);
    //    WikiParserUtils.dumpFragmentTree(chcont, sb, "");
    //    System.out.println("Aftertrans: " + sb.toString());

    List<GWikiFragment> nfrags = chcont.getChilds();
    ctx.pushFragList(nfrags);
  }

  @Override
  public int getPrio()
  {
    return 40;
  }

}

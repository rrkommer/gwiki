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

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.RteDomVisitor;

public class RteHeadingStyleDomElementListener implements DomElementListener
{

  public static void registerListeners(RteDomVisitor visitor)
  {
    for (int i = 0; i < 10; ++i) {
      visitor.registerListener("H" + i, new RteHeadingStyleDomElementListener());

    }
  }

  @Override
  public boolean listen(DomElementEvent event)
  {
    List<GWikiFragment> frags = event.walkCollectChilds();
    GWikiFragmentHeading nfrag = new GWikiFragmentHeading(Integer.parseInt("" + event.getElementName().charAt(1)));
    nfrag.setChilds(frags);
    event.getParseContext().addFragment(nfrag);
    return false;
  }

}

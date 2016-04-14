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

import org.apache.commons.collections15.ArrayStack;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentList;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.utils.StringUtils;

public class RteListDomElementListener implements DomElementListener
{
  protected ArrayStack<String> liStack = new ArrayStack<String>();

  @Override
  public boolean listen(DomElementEvent event)
  {
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.flushText();
    GWikiFragmentList listfrag = new GWikiFragmentList(getListTag(event));
    parseContext.addFragment(listfrag);
    //    parseContext.pushFragStack(listfrag);
    liStack.push(event.getElementName());
    List<GWikiFragment> frags = event.walkCollectChilds();
    listfrag.addChilds(frags);

    liStack.pop();
    //    parseContext.popFragStack();
    return false;
  }

  protected String getListTag(DomElementEvent event)
  {
    String tag;
    if (event.getElementName().equals("OL") == true) {
      tag = "#";
    } else if (StringUtils.equals(event.getAttr("type"), "square") == true) {
      tag = "-";
    } else {
      tag = "*";
    }
    if (liStack.isEmpty() == true) {
      return tag;
    }
    for (int i = 0; i < event.getParseContext().stackSize(); ++i) {
      List<GWikiFragment> fl = event.getParseContext().peek(i);
      if (fl.size() > 0) {
        GWikiFragment lr = fl.get(fl.size() - 1);
        if (lr instanceof GWikiFragmentList) {
          GWikiFragmentList lf = (GWikiFragmentList) lr;
          tag += lf.getListTag();
          break;
        }
      }
    }
    return tag;
  }
}

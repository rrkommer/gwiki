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

package de.micromata.genome.gwiki.page.impl.wiki.rte;

import java.util.List;

import org.w3c.dom.Node;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;

public class DomEvent
{
  public HtmlDomWalker walker;
  public RteDomVisitor visitor;

  public DomEvent(HtmlDomWalker walker, RteDomVisitor visitor)
  {
    super();
    this.walker = walker;
    this.visitor = visitor;
  }

  public GWikiWikiParserContext getParseContext()
  {
    return walker.parseContext;
  }

  public void setCurNode(Node node)
  {
    walker.currentNode = node;
  }

  public void walkChilds()
  {

    walker.walkChilds(visitor);
    walker.parseContext.flushText();
  }

  public List<GWikiFragment> walkCollectChilds()
  {
    walker.parseContext.flushText();
    walker.parseContext.pushFragList();
    walkChilds();
    return walker.parseContext.popFragList();
  }

}

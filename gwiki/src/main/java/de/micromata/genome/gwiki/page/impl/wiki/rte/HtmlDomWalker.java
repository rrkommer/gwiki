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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;

public class HtmlDomWalker implements DomWalker
{
  DocumentFragment document;

  Node currentNode;
  Node startNode;
  GWikiContext wikiContext;

  protected GWikiWikiParserContext parseContext = new GWikiWikiParserContext();
  List<Node> nodeStack = new ArrayList<>();

  public HtmlDomWalker(DocumentFragment document, String currentPageId, GWikiContext wikiContext)
  {
    this.document = document;
    this.wikiContext = wikiContext;
    currentNode = document;
    startNode = currentNode;
    parseContext.setCurrentPageId(currentPageId);
    parseContext.getMacroFactories()
        .putAll(wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext));

  }

  @Override
  public void walk(DomVisitor visitor)
  {
    visitor.visit(this);
    while (nextNode() != null) {
      visitor.visit(this);
    }
  }

  public Node nextNode()
  {
    if (currentNode == null) {
      return null;
    }
    if (startNode == null) {
      startNode = currentNode;
    }
    if (currentNode.hasChildNodes() == true) {
      return currentNode = currentNode.getFirstChild();
    }
    Node nn = currentNode.getNextSibling();
    if (nn != null) {
      return currentNode = nn;
    }
    if (currentNode.getParentNode() == currentNode || currentNode.getParentNode() == null) {
      return null;
    }

    return _nextFromParent();
  }

  private Node _nextFromParent()
  {

    Node pnode = currentNode.getParentNode();
    if (pnode == null || pnode == startNode) {
      return null;
    }
    if (pnode.getNextSibling() != null) {
      return currentNode = pnode.getNextSibling();
    }
    if (pnode.getParentNode() == pnode || pnode.getParentNode() == startNode) {
      return null;
    }
    currentNode = pnode;
    if (currentNode == null) {
      return null;
    }
    return _nextFromParent();

  }

  public void skipChildren()
  {
    if (currentNode.hasChildNodes() == false) {
      return;
    }
    currentNode = lastLastChild(currentNode);
  }

  public void walkChilds(DomVisitor visitor)
  {
    if (currentNode.hasChildNodes() == false) {
      return;
    }
    Node curSicNode = currentNode;
    Node sicNode = startNode;
    startNode = currentNode;
    while (nextNode() != null) {
      visitor.visit(this);
    }
    startNode = sicNode;
    currentNode = curSicNode;
    currentNode = lastLastChild(currentNode);
  }

  public String walkChildsCollectText()
  {
    StringBuilder sb = new StringBuilder();
    walkChilds(new DomVisitor()
    {

      @Override
      public void visit(HtmlDomWalker wk)
      {
        if (wk.currentNode instanceof Text) {
          sb.append(wk.currentNode.getNodeValue());
        }
      }
    });
    return sb.toString();
  }

  public Node lastLastChild(Node node)
  {
    node = node.getLastChild();
    if (node.hasChildNodes() == true) {
      return lastLastChild(node);
    }
    return node;
  }

}

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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.StringUtils;

public class DomElementEvent extends DomEvent
{

  public Element element;

  public DomElementEvent(HtmlDomWalker walker, RteDomVisitor visitor, Element element)
  {
    super(walker, visitor);
    this.element = element;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if (element != null) {
      DomToStringUtils.toString(element, sb);
    }
    return sb.toString();
  }

  public String getAttr(String attrName)
  {
    return getAttr(element, attrName);
  }

  public String getAttr(Node node, String attrName)
  {
    if ((node instanceof Element) == false) {
      return "";
    }
    return StringUtils.defaultString(((Element) node).getAttribute(attrName));
  }

  public String getStyleClass(Node node)
  {
    return getAttr(node, "class");
  }

  public String getStyleClass()
  {
    return getStyleClass(element);

  }

  public String getElementName()
  {
    return element.getNodeName();
  }

  public Element firstChildElement()
  {
    return firstChildElement(element);
  }

  public Element firstChildElement(Element element)
  {
    Node child = element.getFirstChild();
    if (child != null && (child instanceof Element) == false) {
      child = child.getNextSibling();
    }
    if (child instanceof Element) {
      return (Element) child;
    }
    return null;
  }

  public boolean containsInStyleClass(Node node, String text)
  {
    return getStyleClass(node).contains(text);
  }

  public boolean containsInStyleClass(String text)
  {
    return getStyleClass().contains(text);
  }

  public Element getNextElementSibling(Element node)
  {
    Node ret = node.getNextSibling();
    while (ret != null && (ret instanceof Element) == false) {
      ret = ret.getNextSibling();
    }

    return (Element) ret;
  }

  public GWikiContext getWikiContext()
  {
    return walker.wikiContext;
  }

}
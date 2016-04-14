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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DomToStringUtils
{
  public static void attrToString(Element el, StringBuilder sb)
  {
    if (el.hasAttributes() == false) {
      return;
    }
    NamedNodeMap attrs = el.getAttributes();
    for (int i = 0; i < attrs.getLength(); ++i) {
      sb.append(" ");
      Node att = attrs.item(i);
      sb.append(att);

    }
  }

  public static String toString(Node node)
  {
    StringBuilder sb = new StringBuilder();
    toString(node, sb);
    return sb.toString();
  }

  public static void toString(Node node, StringBuilder sb)
  {
    sb.append(node.getClass().getSimpleName());
    if (node instanceof Element) {

      Element el = (Element) node;
      sb.append(":");
      sb.append(el.getNodeName());
      sb.append(" ");
      attrToString(el, sb);
    }
  }
}

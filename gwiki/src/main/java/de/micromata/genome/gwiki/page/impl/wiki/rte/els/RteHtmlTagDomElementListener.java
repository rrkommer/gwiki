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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public abstract class RteHtmlTagDomElementListener extends RteBrDomElementListener
{
  //  @Override
  //  public boolean listen(DomElementEvent event)
  //  {
  //    String en = event.getElementName().toLowerCase();
  //    GWikiWikiParserContext parseContext = event.getParseContext();
  //    parseContext.flushText();
  //    List<GWikiFragment> childs = event.walkCollectChilds();
  //
  //    return false;
  //  }
  protected static boolean ignoreAttribute(String name)
  {
    return false;
  }

  public static void copyAttributes(DomElementEvent event, MacroAttributes macroAttributes)
  {
    NamedNodeMap attrs = event.element.getAttributes();
    for (int i = 0; i < attrs.getLength(); ++i) {
      Node el = attrs.item(i);
      String attrName = el.getNodeName();
      String attrValue = el.getNodeValue();
      if (ignoreAttribute(attrName) == true) {
        continue;
      }
      macroAttributes.getArgs().setStringValue(attrName, attrValue);
    }
  }

  @Override
  public int getPrio()
  {
    return 400;
  }
}

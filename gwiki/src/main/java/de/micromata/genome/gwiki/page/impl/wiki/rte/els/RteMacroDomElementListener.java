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

import org.w3c.dom.Element;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBase;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.utils.StringUtils;

public class RteMacroDomElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    if (event.containsInStyleClass("weditmacroframe") == false) {
      return true;
    }
    event.getParseContext().flushText();
    Element headNode = event.firstChildElement();
    if (event.containsInStyleClass(headNode, "weditmacrohead") == false) {
      event.walker.skipChildren();
      return false;
    }
    String macroHead = event.getAttr(headNode, "data-macrohead");
    if (StringUtils.isBlank(macroHead) == true) {
      event.walker.skipChildren();
      return false;
    }
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.resetText();
    MacroAttributes attrs = new MacroAttributes(macroHead);
    GWikiMacroFragment macroFragment = parseContext.createMacro(attrs);
    Element bodyNode = event.getNextElementSibling(headNode);
    if (bodyNode == null) {
      if (macroFragment.getMacro().hasBody() == true) {
        // todo warn
      }
      parseContext.addFragment(macroFragment);
      event.walker.skipChildren();
      return false;
    }
    parseContext.addFragment(macroFragment);
    if (macroFragment.getMacro().hasBody() == true) {

      if (macroFragment.getMacro().evalBody() == true) {
        event.setCurNode(bodyNode);
        List<GWikiFragment> childs = event.walkCollectChilds();
        macroFragment.addChilds(childs);
        return false;
      } else {
        event.walker.skipChildren();
        parseContext.resetText();
        event.setCurNode(bodyNode);
        String text = event.walker.walkChildsCollectText();
        if (GWikiMacroRenderFlags.TrimTextContent.isSet(macroFragment.getRenderModes()) == true) {
          text = GWikiFragmentBase.trimWhitespaceNl(text);
        }
        macroFragment.getAttrs().setBody(text);
      }
    }
    return false;
  }

}

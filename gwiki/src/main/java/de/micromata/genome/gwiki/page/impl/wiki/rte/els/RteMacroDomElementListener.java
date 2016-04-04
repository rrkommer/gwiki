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

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

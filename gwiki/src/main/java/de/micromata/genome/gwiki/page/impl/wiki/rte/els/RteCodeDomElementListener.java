package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class RteCodeDomElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.flushText();
    String cls = event.getStyleClass();
    String lang = StringUtils.substringAfter(cls, "language-");
    String text = event.walker.walkChildsCollectText();
    String header = "code:lang=" + lang;
    MacroAttributes attrs = new MacroAttributes(header);
    attrs.setBody(text);
    GWikiMacroFragment macroFragment = parseContext.createMacro(attrs);
    parseContext.addFragment(macroFragment);
    return false;
  }

  @Override
  public int getPrio()
  {
    return 5;
  }

}

package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTextDeco;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlInSpanBodyTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class RteSpanDomElementListener extends RteHtmlTagDomElementListener
{
  @Override
  public boolean listen(DomElementEvent event)
  {
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.flushText();
    List<GWikiFragment> frags = event.walkCollectChilds();
    boolean isSimpleChild = false;
    if (frags.size() == 1 && frags.get(0) instanceof GWikiFragmentText) {
      isSimpleChild = true;
    }
    String cls = event.getStyleClass();
    String style = event.getAttr("style");
    if (isSimpleChild == true) {
      if (StringUtils.isBlank(cls) == true) {
        if (StringUtils.isNotBlank(style) == true) {
          if (style.equals("text-decoration: underline;") == true) {
            GWikiFragmentTextDeco fragDeco = new GWikiFragmentTextDeco('+',
                "<u>",
                "</u>",
                frags);
            fragDeco.setRequireMacroSyntax(
                RteSimpleStyleDomElementListener.requireTextDecoMacroSyntax(fragDeco, parseContext));
            parseContext.addFragment(fragDeco);
            return false;
          }
        }
      }
    }
    MacroAttributes matts = new MacroAttributes(event.getElementName().toLowerCase());
    copyAttributes(event, matts);

    GWikiHtmlInSpanBodyTagMacro macro = new GWikiHtmlInSpanBodyTagMacro();
    GWikiMacroFragment gWikiMacroFragment = new GWikiMacroFragment(macro, matts);
    gWikiMacroFragment.addChilds(frags);
    parseContext.addFragment(gWikiMacroFragment);
    return false;
  }

  @Override
  public int getPrio()
  {
    return 200;
  }

}

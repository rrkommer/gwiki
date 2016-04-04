package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyPTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.utils.StringUtils;

public class RtePDomElementListener extends RteBrDomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.flushText();
    String styleClass = event.getAttr("class");
    String style = event.getAttr("style");
    if (StringUtils.isNotBlank(styleClass) == true || StringUtils.isNotBlank(style) == true) {
      MacroAttributes ma = new MacroAttributes();
      ma.setCmd("p");
      if (StringUtils.isNotBlank(styleClass) == true) {
        ma.getArgs().setStringValue("class", styleClass);
      }
      if (StringUtils.isNotBlank(style) == true) {
        ma.getArgs().setStringValue("style", style);
      }
      GWikiMacroFragment mf = new GWikiMacroFragment(new GWikiHtmlBodyPTagMacro(), ma);
      mf.addChilds(event.walkCollectChilds());
      parseContext.addFragment(mf);
    } else {
      List<GWikiFragment> childs = event.walkCollectChilds();
      parseContext.addFragment(new GWikiFragmentP(childs));
      //      
      //      if (parseContext.lastFragment() != null
      //          && GWikiMacroRenderFlags.NewLineBeforeEnd.isSet(parseContext.lastFragment().getRenderModes()) == true) {
      //        parseContext.addFragments(childs);
      //        parseContext.addFragment(new GWikiFragmentBr());
      //      } else {
      //        if (hasPreviousBr(parseContext) == false) {
      //          parseContext.addFragment(getNlFragement(parseContext, new GWikiFragmentP(childs)));
      //        } else {
      //          parseContext.addFragments(childs);
      //          parseContext.addFragment(getNlFragement(parseContext, new GWikiFragmentBr()));
      //        }
      //      }

    }
    return false;
  }

  protected boolean hasPreviousBr(GWikiWikiParserContext parseContext)
  {

    // last character field has br
    if (parseContext.lastFragment() instanceof GWikiFragmentBr) {
      return true;
    }
    return false;
  }
}

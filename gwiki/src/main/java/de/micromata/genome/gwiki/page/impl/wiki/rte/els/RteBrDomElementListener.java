package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBr;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;

public class RteBrDomElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    event.getParseContext().flushText();
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.addFragment(getNlFragement(parseContext, new GWikiFragmentBr()));
    return false;
  }

  @SuppressWarnings("unchecked")
  public boolean needSoftNl(GWikiWikiParserContext parseContext)
  {
    //return parseContext.findFragsInStack(GWikiFragmentLi.class, GWikiFragmentTable.class) != null;
    return false;
  }

  public GWikiFragment getNlFragement(GWikiWikiParserContext parseContext, GWikiFragment defaultFrag)
  {
    //    if (needSoftNl(parseContext) == true) {
    return new GWikiFragmentBr();
    //    }
    //    return defaultFrag;
  }
}

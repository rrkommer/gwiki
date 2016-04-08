package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentFixedFont;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;

public class RteCodeFormatDomElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.flushText();
    List<GWikiFragment> childs = event.walkCollectChilds();
    parseContext.addFragment(new GWikiFragmentFixedFont(childs));
    return false;
  }

}

package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLi;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentList;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;

public class RteLiDomentElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.flushText();
    GWikiFragmentList parentList = parseContext.findFragInStack(GWikiFragmentList.class);

    GWikiFragmentLi lifrag = new GWikiFragmentLi(parentList);
    List<GWikiFragment> childs = event.walkCollectChilds();
    lifrag.setChilds(childs);
    parseContext.addFragment(lifrag);
    return false;
  }

}

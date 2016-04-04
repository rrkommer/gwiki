package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.RteDomVisitor;

public class RteHeadingStyleDomElementListener implements DomElementListener
{

  public static void registerListeners(RteDomVisitor visitor)
  {
    for (int i = 0; i < 10; ++i) {
      visitor.registerListener("H" + i, new RteHeadingStyleDomElementListener());

    }
  }

  @Override
  public boolean listen(DomElementEvent event)
  {
    List<GWikiFragment> frags = event.walkCollectChilds();
    GWikiFragmentHeading nfrag = new GWikiFragmentHeading(Integer.parseInt("" + event.getElementName().charAt(1)));
    nfrag.setChilds(frags);
    event.getParseContext().addFragment(nfrag);
    return false;
  }

}

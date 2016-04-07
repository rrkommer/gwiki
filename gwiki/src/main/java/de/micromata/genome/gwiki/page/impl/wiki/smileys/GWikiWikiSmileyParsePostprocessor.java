package de.micromata.genome.gwiki.page.impl.wiki.smileys;

import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParsePostprocessor;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;

public class GWikiWikiSmileyParsePostprocessor implements GWikiWikiParsePostprocessor
{
  @Override
  public void process(GWikiWikiParserContext ctx)
  {
    GWikiContext wikiContext = GWikiContext.getCurrent();
    if (wikiContext == null) {
      return;
    }

    List<GWikiFragment> frags = ctx.popFragList();
    GWikiFragmentChildContainer chcont = new GWikiFragmentChildContainer(frags);

    chcont.iterate(new GWikiSmileyContentIterator(wikiContext), null);
    List<GWikiFragment> nfrags = chcont.getChilds();
    ctx.pushFragList(nfrags);
  }

  @Override
  public int getPrio()
  {
    return 200;
  }
}

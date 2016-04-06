package de.micromata.genome.gwiki.page.impl.wiki.smileys;

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParsePostprocessor;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;

public class GWikiWikiSmileyParsePostprocessor implements GWikiWikiParsePostprocessor
{
  @Override
  public void process(GWikiWikiParserContext ctx)
  {
    List<GWikiFragment> frags = ctx.popFragList();
    GWikiFragmentChildContainer chcont = new GWikiFragmentChildContainer(frags);

    chcont.iterate(new GWikiSmileyContentIterator(), null);
    List<GWikiFragment> nfrags = chcont.getChilds();
    ctx.pushFragList(nfrags);
  }

  @Override
  public int getPrio()
  {
    return 200;
  }
}

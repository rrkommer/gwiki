package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserUtils.RevisePsVisitor;

public class GWikiWikiParagraphParsePostprocessor implements GWikiWikiParsePostprocessor
{

  @Override
  public void process(GWikiWikiParserContext ctx)
  {
    if (GWikiWikiParser.isPAllowedInDom(ctx) == false) {
      return;
    }
    List<GWikiFragment> l = ctx.popFragList();
    if (l.isEmpty() == true) {
      ctx.pushFragList(l);
      return;
    }
    l = GWikiWikiParser.addWrappedP(l);
    ctx.pushFragList(l);

    List<GWikiFragment> frags = ctx.popFragList();
    GWikiFragmentChildContainer chcont = new GWikiFragmentChildContainer(frags);
    //    StringBuilder sb = new StringBuilder();
    //    WikiParserUtils.dumpFragmentTree(chcont, sb, "");
    //    System.out.println("Beforetrans: " + sb.toString());
    //    sb.setLength(0);
    RevisePsVisitor visit = new RevisePsVisitor();
    chcont.iterate(visit, null);
    //    WikiParserUtils.dumpFragmentTree(chcont, sb, "");
    //    System.out.println("Aftertrans: " + sb.toString());

    List<GWikiFragment> nfrags = chcont.getChilds();
    ctx.pushFragList(nfrags);
  }

  @Override
  public int getPrio()
  {
    return 40;
  }

}

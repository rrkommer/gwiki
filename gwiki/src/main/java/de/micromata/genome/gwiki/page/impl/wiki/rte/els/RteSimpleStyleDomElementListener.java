package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTextDeco;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.RteDomVisitor;
import de.micromata.genome.gwiki.utils.StringUtils;

public class RteSimpleStyleDomElementListener implements DomElementListener
{
  public static Map<String, String> DefaultSimpleTextDecoMap = new HashMap<String, String>();
  static {

    DefaultSimpleTextDecoMap.put("B", "*");
    DefaultSimpleTextDecoMap.put("STRONG", "*");
    DefaultSimpleTextDecoMap.put("EM", "_");
    DefaultSimpleTextDecoMap.put("I", "_");
    DefaultSimpleTextDecoMap.put("DEL", "-");
    DefaultSimpleTextDecoMap.put("STRIKE", "-");
    DefaultSimpleTextDecoMap.put("SUB", "~");
    DefaultSimpleTextDecoMap.put("SUP", "^");
    DefaultSimpleTextDecoMap.put("U", "+");
  }

  public static void registerListeners(RteDomVisitor visitor)
  {
    for (String key : DefaultSimpleTextDecoMap.keySet()) {
      visitor.registerListener(key, new RteSimpleStyleDomElementListener());

    }
  }

  @Override
  public boolean listen(DomElementEvent event)
  {
    List<GWikiFragment> frags = event.walkCollectChilds();
    String en = event.getElementName();

    GWikiFragmentTextDeco fragDeco = new GWikiFragmentTextDeco(DefaultSimpleTextDecoMap.get(en).charAt(0),
        "<" + en + ">",
        "</" + en + ">",
        frags);
    fragDeco.setRequireMacroSyntax(requireTextDecoMacroSyntax(fragDeco, event.getParseContext()));
    event.getParseContext().addFragment(fragDeco);
    return false;
  }

  public static boolean requireTextDecoMacroSyntax(final GWikiFragmentTextDeco fragDeco,
      GWikiWikiParserContext parseContext)
  {
    fragDeco.iterate(new GWikiSimpleFragmentVisitor()
    {

      @Override
      public void begin(GWikiFragment fragment)
      {
        if (fragDeco.isRequireMacroSyntax() == true) {
          return;
        }
        if (fragment instanceof GWikiFragmentP || fragment instanceof GWikiFragmentBr) {
          fragDeco.setRequireMacroSyntax(true);
        }
      }
    }, null);
    if (fragDeco.isRequireMacroSyntax() == true) {
      return true;
    }
    GWikiFragment lf = parseContext.lastFrag();
    if (lf == null) {
      return false;
    }
    if ((lf instanceof GWikiFragmentText) == false) {
      return false;
    }
    GWikiFragmentText tl = (GWikiFragmentText) lf;
    String source = tl.getSource();
    if (StringUtils.isEmpty(source) == true) {
      return false;
    }
    char lc = source.charAt(source.length() - 1);
    if (Character.isSpace(lc) == false) {
      return true;
    }
    return false;
  }

  @Override
  public int getPrio()
  {
    return 200;
  }

}

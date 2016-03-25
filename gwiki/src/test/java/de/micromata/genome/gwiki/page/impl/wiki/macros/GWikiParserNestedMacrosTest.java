package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.List;

import org.junit.Test;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki2.GWikiWikiParserTestBase;

public class GWikiParserNestedMacrosTest extends GWikiWikiParserTestBase
{
  //  @Test
  public void xtestNestedDivs()
  {
    List<GWikiFragment> frags = parseText("{div:x=y}{div:x=z}asdf{div}{div}", stdMacroFactories);
  }

  @Test
  public void testNestedSpans()
  {
    List<GWikiFragment> frags = parseText("{span:x=y}{span:x=z}asdf{span}{span}", stdMacroFactories);
  }
}

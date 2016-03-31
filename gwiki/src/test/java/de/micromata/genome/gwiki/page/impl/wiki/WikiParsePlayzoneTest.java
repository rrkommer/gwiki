package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.Map;

import org.junit.Test;

import de.micromata.genome.gwiki.page.impl.wiki.macros.registry.GWikiBuildinProviderServiceImpl;
import de.micromata.genome.gwiki.page.impl.wiki2.GWikiWikiParserTestBase;

public class WikiParsePlayzoneTest extends GWikiWikiParserTestBase
{
  Map<String, GWikiMacroFactory> macroFactories = new GWikiBuildinProviderServiceImpl().getMacros();

  //  @Test
  public void xtestParsePs()
  {

    String wiki = "h2. Title\nFirst\n\nSecond\nThird";
    String genHtml = wiki2html(wiki, macroFactories);
    System.out.println("W2H: " + wiki + "\nHTML:\n" + genHtml);
  }

  @Test
  public void testParseDivs()
  {
    String wiki = "h2. Title\n{div}\nFirst\nSecond{div}";
    String genHtml = wiki2html(wiki, macroFactories);
    System.out.println("W2H: " + wiki + "\nHTML:\n" + genHtml);
    parseDumpWiki(wiki, macroFactories);
  }
}

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.12.2009
// Copyright Micromata 22.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki2;

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentDecorator;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentList;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;

public class GWikiWikiParserTextDecoTest extends GWikiWikiParserTestBase
{
  public void testDeco6()
  {
    // -Effekten-
    String html = wiki2html("Durchgestrichen -Effekt-.");
    assertEquals("Durchgestrichen <del>Effekt</del>.<br/>\n", html);
  }
  public void testDeco5()
  {
    // -Effekten-
    String html = wiki2html("Durchgestrichen -Effekt-");
    assertEquals("Durchgestrichen <del>Effekt</del>", html);
  }
  public void testDeco4()
  {
    String html = wiki2html("Doc -Node und -x");
    assertEquals("Doc -Node und -x", html);
  }
  public void testDeco3()
  {
    String html = wiki2html("Doc -Node und -");
    assertEquals("Doc -Node und -", html);
  }
  public void testDeco2()
  {
    String html = wiki2html("Doc-Node und -");
    assertEquals("Doc-Node und -", html);
  }
  public void testPreFormat()
  {
    String html = wiki2html("{{asdf}}");
    assertEquals("<span style=\"font-family:monospace\">asdf</span>", html);
  }
  public void testUnterminatedDecHtml()
  {
    String html = wiki2html("_a\nx");
    assertEquals("_a<br/>\nx", html);
  }

  public void testNestedDecHtml()
  {
    String html = wiki2html("*_a_*");
    assertEquals("<b><em>a</em></b>", html);
  }

  public void testNestedDec()
  {
    String wikiText = "*_a_*";
    List<GWikiFragment> frags = parseText(wikiText);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentDecorator);
    GWikiFragmentDecorator fragDec = (GWikiFragmentDecorator) frags.get(0);
    assertTrue(fragDec.getPrefix().equals("<b>"));
    GWikiFragment nf = fragDec.getChilds().get(0);
    assertTrue(nf instanceof GWikiFragmentDecorator);
    fragDec = (GWikiFragmentDecorator) nf;
    assertTrue(fragDec.getPrefix().equals("<em>"));
    nf = fragDec.getChilds().get(0);
    assertTrue(((GWikiFragmentText) nf).getHtml().equals("a"));
    // assertTrue(((GWikiFragmentText) fragDec.getChilds().get(0)).getHtml().equals("a"));
  }

  public void testSimpleDec()
  {
    String wikiText = "*a*";
    List<GWikiFragment> frags = parseText(wikiText);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentDecorator);
    GWikiFragmentDecorator fragDec = (GWikiFragmentDecorator) frags.get(0);
    assertTrue(fragDec.getPrefix().equals("<b>"));
    assertTrue(((GWikiFragmentText) fragDec.getChilds().get(0)).getHtml().equals("a"));
  }

  public void testLi1()
  {
    String wikiText = "* a\n* b";
    List<GWikiFragment> frags = parseText(wikiText);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentList);
    GWikiFragmentList listFrag = (GWikiFragmentList) frags.get(0);
    assertEquals(2, listFrag.getChilds().size());
  }

  public void testIt()
  {
    String wikiText = "a";
    List<GWikiFragment> frags = parseText(wikiText);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentText);
    assertTrue(((GWikiFragmentText) frags.get(0)).getHtml().equals("a"));
  }
}

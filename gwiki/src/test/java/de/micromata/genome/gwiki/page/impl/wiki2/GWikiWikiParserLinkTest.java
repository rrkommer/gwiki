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

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragementLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentDecorator;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;

public class GWikiWikiParserLinkTest extends GWikiWikiParserTestBase
{
  public void testSingleLinkWithEscapedTitel()
  {
    List<GWikiFragment> frags = parseText("[Ein\\|Titel|ein/link]");
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragementLink);
    GWikiFragementLink l = (GWikiFragementLink) frags.get(0);
    assertEquals("ein/link", l.getTarget());
    frags = l.getChilds();
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentText);
    assertEquals("Ein|Titel", ((GWikiFragmentText) frags.get(0)).getSource());
  }
  public void testSingleLinkWithBoldTitle()
  {
    List<GWikiFragment> frags = parseText("[*Ein Titel*|ein/link]");
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragementLink);
    GWikiFragementLink l = (GWikiFragementLink) frags.get(0);
    assertEquals("ein/link", l.getTarget());
    frags = l.getChilds();
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentDecorator);
    GWikiFragmentDecorator dec = (GWikiFragmentDecorator) frags.get(0);
    assertEquals("<b>", dec.getPrefix());
    frags = dec.getChilds();
    assertEquals(1, frags.size());
    assertEquals("Ein Titel", ((GWikiFragmentText) frags.get(0)).getSource());
  }

  public void testSingleLinkWithTitle()
  {
    List<GWikiFragment> frags = parseText("[Ein Titel|ein/link]");
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragementLink);
    GWikiFragementLink l = (GWikiFragementLink) frags.get(0);
    assertEquals("ein/link", l.getTarget());
    frags = l.getChilds();
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentText);
    assertEquals("Ein Titel", ((GWikiFragmentText) frags.get(0)).getSource());
  }

  public void testSingleLink()
  {
    List<GWikiFragment> frags = parseText("[ein/link]");
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragementLink);
    GWikiFragementLink l = (GWikiFragementLink) frags.get(0);
    assertEquals("ein/link", l.getTarget());
  }

}

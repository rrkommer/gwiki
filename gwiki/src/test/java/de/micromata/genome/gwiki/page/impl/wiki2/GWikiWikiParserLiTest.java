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

public class GWikiWikiParserLiTest extends GWikiWikiParserTestBase
{
  public void testNl()
  {
    w2htest("* bla\\\\\nx\n* blub", "<ul class=\"star\"><li>bla<br/>\nx</li><li>blub</li></ul>");
  }
  public void testNested()
  {
    w2htest("* bla\n** blub", "<ul class=\"star\"><li>bla</li><ul class=\"star\"><li>blub</li></ul></ul>");
  }

  public void testLiCh()
  {
    w2htest("- bla\n* blub", "<ul class=\"minus\" type=\"square\"><li>bla</li></ul><ul class=\"star\"><li>blub</li></ul>");
  }

  public void testLi5()
  {
    w2htest("# bla\n# blub", "<ol><li>bla</li><li>blub</li></ol>");
  }

  public void testLi4()
  {
    w2htest("- bla\n- blub", "<ul class=\"minus\" type=\"square\"><li>bla</li><li>blub</li></ul>");
  }

  public void testLi3()
  {
    w2htest("* bla\n\n* blub", "<ul class=\"star\"><li>bla</li></ul><br/>\n<ul class=\"star\"><li>blub</li></ul>");
  }

  public void testLi2()
  {
    w2htest("* bla\n* blub", "<ul class=\"star\"><li>bla</li><li>blub</li></ul>");
  }

  public void testLi1()
  {
    w2htest("* bla", "<ul class=\"star\"><li>bla</li></ul>");
  }
}

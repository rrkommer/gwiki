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


public class GWikiWikiParserNlTest extends GWikiWikiParserTestBase
{
  public void testhr()
  {
    w2htest("x\n----\nb", "x<br/>\n<hr/>\nb");
  }
  public void testBr()
  {
    w2htest("a.\nb", "a.<br/>\nb");
  }

  public void testExpliciteNl()
  {
    w2htest("a\\\\\nb", "a<br/>\nb");
  }
  
  public void testNonExpliciteNl()
  {
    w2htest("a\nb", "a<br/>\nb");
  }
  
  

}

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

public class GWikiWikiParserHeadingTest extends GWikiWikiParserTestBase
{
  public void testHeadingWithText()
  {
    w2htest("h1. Heading\nText", "<h1><a name=\"Heading\" target=\"_top\"></a>Heading</h1>\nText");
  }
  public void testHeadingWithEffect()
  {
    w2htest("h1. *Heading*", "<h1><a name=\"Heading\" target=\"_top\"></a><b>Heading</b></h1>\n");
  }
  public void testHeading()
  {
    w2htest("h1. Heading", "<h1><a name=\"Heading\" target=\"_top\"></a>Heading</h1>\n");
  }
 
}

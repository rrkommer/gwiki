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

import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;
import junit.framework.TestCase;

public class GWikiWikiTokensTest extends TestCase
{
  public void testIt()
  {
    GWikiWikiTokens tkns = new GWikiWikiTokens("\n-*_", "a");
    while (tkns.hasNext()) {
      char c = tkns.nextToken();
      String s = tkns.curTokenString();
      System.out.println("[" + c + "] " + s);
    }
  }
  public void testIt2()
  {
    GWikiWikiTokens tkns = new GWikiWikiTokens("\n-*_", "Abcb-c-");
    while (tkns.hasNext()) {
      char c = tkns.nextToken();
      String s = tkns.curTokenString();
      System.out.println("[" + c + "] " + s);
    }
  }
}

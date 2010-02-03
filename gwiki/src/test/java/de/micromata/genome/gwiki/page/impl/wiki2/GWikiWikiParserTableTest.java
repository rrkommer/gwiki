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

public class GWikiWikiParserTableTest extends GWikiWikiParserTestBase
{
  public void testTableNested()
  {
    //w2htest("||K1||", "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th>\n</tr></tbody></table>");
    w2htest("||* K1||", "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\"><ul class=\"star\"><li>K1</li></ul></th>\n</tr></tbody></table>");
  }
  public void testTableFail1()
  {
    w2htest("||K1", "||K1");
  }
  public void testTable4()
  {
    w2htest("||K1||K2||\n|Z1\\\\\nY|Z2|\n", //
        "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th><th class=\"gwikith\">K2</th>\n</tr><tr>\n<td class=\"gwikitd\">Z1<br/>\nY</td><td class=\"gwikitd\">Z2</td>\n</tr></tbody></table>");
  }
  public void testTable3()
  {
    w2htest(
        "werden.\n||K1||K2||\n|h1. Z1|Z2|\n", //
        "werden.<br/>\n<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th><th class=\"gwikith\">K2</th>\n</tr><tr>\n<td class=\"gwikitd\"><h1><a name=\"Z1\" target=\"_top\"></a>Z1</h1>\n</td><td class=\"gwikitd\">Z2</td>\n</tr></tbody></table>");
  }

  public void testTableWithHeading()
  {
    w2htest(
        "||K1||K2||\n|h1. Z1|Z2|\n", //
        "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th><th class=\"gwikith\">K2</th>\n</tr><tr>\n<td class=\"gwikitd\"><h1><a name=\"Z1\" target=\"_top\"></a>Z1</h1>\n</td><td class=\"gwikitd\">Z2</td>\n</tr></tbody></table>");
  }

  public void testSimpleTable()
  {
    w2htest("||K1||K2||\n|Z1|Z2|\n", //
        "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th><th class=\"gwikith\">K2</th>\n</tr><tr>\n<td class=\"gwikitd\">Z1</td><td class=\"gwikitd\">Z2</td>\n</tr></tbody></table>");
  }

}

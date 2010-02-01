/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.11.2009
// Copyright Micromata 18.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.micromata.genome.gwiki.utils.CommaListParser;

public class CommaListParserTest extends TestCase
{
  public void parse(String input, String... expected)
  {
    List<String> ret = CommaListParser.parseCommaList(input);
    List<String> expectedL = new ArrayList<String>();
    for (int i = 0; i < expected.length; ++i) {
      expectedL.add(expected[i]);
    }
    if (expectedL.equals(ret) == false) {
      fail("expected: " + expectedL + "; got: " + ret);
    }
  }

  public void testIt()
  {
    parse("\"a\" , ", "a", "");
    parse("  ");
    parse(",", "", "");
    parse("\"a\"", "a");
    parse("\"a,b\"", "a,b");
    parse("\"(Rechte{0,1})\"", "(Rechte{0,1})");
    parse(null);
    parse("");
    
    
    parse(" , ", "", "");
    parse("a, ", "a", "");
    
    parse("\" a\" , ", " a", "");
    parse("\" a\", b ", " a", "b");
    parse("a,b", "a", "b");

    parse("a", "a");
    parse("a, b", "a", "b");
    parse("a\\b, c", "a\\b", "c");
    parse(" a, b ", "a", "b");

    parse("\", a\", b ", ", a", "b");
    parse("\"\\\" a\", b ", "\" a", "b");
    parse("\"\\\" a\", b ", "\" a", "b");

  }
}

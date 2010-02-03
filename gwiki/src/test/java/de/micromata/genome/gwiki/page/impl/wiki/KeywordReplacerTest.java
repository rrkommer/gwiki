/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   17.11.2009
// Copyright Micromata 17.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class KeywordReplacerTest extends TestCase
{
  void testMatcher(String pattern, String text, String expected)
  {
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(text);
    boolean found = m.find();
    if (found == false && expected == null) {
      return;
    }
    if (expected == null) {
      fail("Pattern '" + pattern + "' with Text '" + text + "' should NOT matched");
    }
    if (found == false) {
      fail("Pattern '" + pattern + "' with Text '" + text + "' should matched with: " + expected);
    }
    if (expected.equals(m.group(0)) == false) {
      fail("Pattern '" + pattern + "' with Text '" + text + "' should matched with: '" + expected + "' but got: '" + m.group(0) + "'");
    }
  }

  public void testIt()
  {
    testMatcher("((?:Rechte)|(?:Recht))", "Recht.", "Recht");
    testMatcher("((?:Rechte)|(?:Recht))", "Rechte.", "Rechte");
    testMatcher("(Rechte{0,1})", " Recht.", "Recht");
    testMatcher("(Rechte{0,1})", " Rechte.", "Rechte");
  }
}

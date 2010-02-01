/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   23.12.2009
// Copyright Micromata 23.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki2;

import junit.framework.Test;
import junit.framework.TestSuite;

public class GWikiWikiParserTestSuite extends TestSuite
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(GWikiWikiParserMacroTest.class);
    suite.addTestSuite(GWikiWikiTokensTest.class);
    suite.addTestSuite(GWikiWikiParserTextDecoTest.class);
    suite.addTestSuite(GWikiWikiParserNlTest.class);
    suite.addTestSuite(GWikiWikiParserLiTest.class);
    suite.addTestSuite(GWikiWikiParserLinkTest.class);
    suite.addTestSuite(GWikiWikiParserHeadingTest.class);
    suite.addTestSuite(GWikiWikiParserTableTest.class);
    
    return suite;
  }
}

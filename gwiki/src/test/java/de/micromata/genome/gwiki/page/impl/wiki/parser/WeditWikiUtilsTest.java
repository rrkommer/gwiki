//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.impl.wiki.parser;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import de.micromata.genome.gwiki.GWikiTestBuilder;
import de.micromata.genome.gwiki.page.GWikiContext;

public class WeditWikiUtilsTest
{
  public static String rteToWiki(GWikiContext ctx, String html)
  {
    String ret = WeditWikiUtils.rteToWiki(ctx, null, html);
    return ret;
  }

  //  @Test
  public void testRte2Wiki()
  {

    String html = "p>&nbsp;</p>\r\n" +
        "<div class=\"mceNonEditable weditmacroframe\">\r\n" +
        "<div class=\"mceNonEditable weditmacrohead\" data-macrohead=\"code:lang=java\" data-macroname=\"code\"><span class=\"weditmacrn\">code:lang=java</span></div>\r\n"
        +
        "<div class=\"mceEditable weditmacrobody editmacrobd_pre\" tabindex=\"-1\">\r\n" +
        "<pre>asdfasdfasdfxxx</pre>\r\n" +
        "</div>\r\n" +
        "</div>";
    GWikiTestBuilder tb = new GWikiTestBuilder();
    GWikiContext ctx = tb.createWikiContext();
    //    GWikiStandaloneContext ctx = GWikiLoader.getStandaloneContext();
    String ret = rteToWiki(ctx, html);
    System.out.println("wiki:\n" + ret);
    ;
  }

  void testConvert(String html, String wiki)
  {
    GWikiTestBuilder tb = new GWikiTestBuilder();
    GWikiContext ctx = tb.createWikiContext();
    try {
      String ret = rteToWiki(ctx, html);
      System.out.println("html:\n" + html + "\nwiki:\n[" + ret + "]");
      if (StringUtils.equals(wiki, ret) == false) {
        System.out.println("expect wiki:\n[" + wiki + "]");
      }
      Assert.assertEquals(wiki, ret);
    } catch (RuntimeException ex) {
      ex.printStackTrace();
      throw ex;
    }
  }

  //  @Test
  public void testUnderscore()
  {
    testConvert("<i>asdf</i>", "_asdf_");
  }

  //  @Test
  public void testHeading()
  {
    testConvert("<h2>asdf</h2>", "h2. asdf\n");
  }

  @Test
  public void testUl()
  {
    testConvert("<ul><li>Text</li></ul>", "* Text\n");
    testConvert("<ol><li>Text</li></ol>", "# Text\n");
    testConvert("<ul><li>Text</li><li>Text 2</li></ul>", "* Text\n* Text 2\n");
  }

  @Test
  public void testSimple()
  {
    String html = "<table class=\"gwikiTable\"><tr><td class=\"gwikitd\">x</td></tr></table>";
    String wiki = "|x|\n";
    testConvert(html, wiki);
  }

  @Test
  public void testTable()
  {
    String html = "<table class=\"gwikiTable\"><tr><td class=\"gwikitd\"><table class=\"gwikiTable\"><tr><td class=\"gwikitd\">x</td></tr></table></td></tr></table>";
    String wiki = "{table:class=gwikiTable}\n{tr}\n{td:class=gwikitd}|x|\n{td}\n{tr}\n{table}";
    testConvert(html, wiki);
  }

  @Test
  public void test2()
  {
    String html = "<html><body><p>&nbsp;</p>\r\n" +
        "<div class=\"mceNonEditable weditmacroframe\">\r\n" +
        "<div class=\"mceNonEditable weditmacrohead\" data-macrohead=\"code:lang=java\" data-macroname=\"code\"><span class=\"weditmacrn\">code:lang=java</span></div>\r\n"
        +
        "<div class=\"mceEditable weditmacrobody editmacrobd_pre\" tabindex=\"-1\">\r\n" +
        "<pre>&nbsp;class X {\r\n" +
        "String foo()\r\n" +
        "{\r\n" +
        "  return \"asdf\";\r\n" +
        "}\r\n" +
        "}</pre>\r\n" +
        "</div>\r\n" +
        "</div>\r\n" +
        "<p>noch was:</p></body></html>";
    GWikiTestBuilder tb = new GWikiTestBuilder();
    GWikiContext ctx = tb.createWikiContext();
    try {
      String ret = rteToWiki(ctx, html);
      System.out.println("wiki:\n" + ret);
    } catch (RuntimeException ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
}

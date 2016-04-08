package de.micromata.genome.gwiki.page.impl.wiki.parser;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import de.micromata.genome.gwiki.GWikiTestBuilder;
import de.micromata.genome.gwiki.page.GWikiContext;

public class WeditWikiUtilsTest
{
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
    String ret = WeditWikiUtils.rteToWiki(ctx, html);
    System.out.println("wiki:\n" + ret);
    ;
  }

  void testConvert(String html, String wiki)
  {
    GWikiTestBuilder tb = new GWikiTestBuilder();
    GWikiContext ctx = tb.createWikiContext();
    try {
      String ret = WeditWikiUtils.rteToWiki(ctx, html);
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
      String ret = WeditWikiUtils.rteToWiki(ctx, html);
      System.out.println("wiki:\n" + ret);
    } catch (RuntimeException ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
}

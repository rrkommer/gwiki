package de.micromata.genome.gwiki.page.impl.wiki.parser;

import org.junit.Test;

import de.micromata.genome.gwiki.GWikiLoader;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;

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
    GWikiStandaloneContext ctx = GWikiLoader.getStandaloneContext();
    String ret = WeditWikiUtils.rteToWiki(ctx, html);
    System.out.println("wiki:\n" + ret);
    ;
  }

  @Test
  public void test2()
  {
    String html = "<p>&nbsp;</p>\r\n" +
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
        "<p>noch was:</p>";
    GWikiStandaloneContext ctx = GWikiLoader.getStandaloneContext();
    String ret = WeditWikiUtils.rteToWiki(ctx, html);
    System.out.println("wiki:\n" + ret);
  }
}

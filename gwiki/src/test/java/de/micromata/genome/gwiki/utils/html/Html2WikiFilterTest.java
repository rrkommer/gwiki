////////////////////////////////////////////////////////////////////////////

// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

////////////////////////////////////////////////////////////////////////////


// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

////////////////////////////////////////////////////////////////////////////


package de.micromata.genome.gwiki.utils.html;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class Html2WikiFilterTest extends TestCase
{
  protected String transform(String html)
  {
    return Html2WikiFilter.html2Wiki(html);
  }

  protected String transform(String html, String... htmle)
  {
    Set<String> s = new HashSet<String>();
    for (String h : htmle) {
      s.add(h);
    }
    return Html2WikiFilter.html2Wiki(html, s);
  }
  public void testTable4Nested2()
  {
    String html = "<table><tr><td><table><tr><td>x</td></tr></table></td></tr></table>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("{table:class=gwikiTable}\n{tr:class=gwikitr}\n{td:class=gwikitd}\n{table:class=gwikiTable}\n{tr:class=gwikitr}\n{td:class=gwikitd}\nx\n{td}\n{tr}\n{table}\n{td}\n{tr}\n{table}", wiki);
  }
  public void testTable4Nested()
  {
    String html = "<table class=\"gwikiTable\"><tr><td class=\"gwikitd\"><table class=\"gwikiTable\"><tr><td class=\"gwikitd\">x</td></tr></table></td></tr></table>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("{table:class=gwikiTable}\n{tr:class=gwikitr}\n{td:class=gwikitd}\n{table:class=gwikiTable}\n{tr:class=gwikitr}\n{td:class=gwikitd}\nx\n{td}\n{tr}\n{table}\n{td}\n{tr}\n{table}", wiki);
  }

  public void testImage2()
  {
    String html = "<img src=\"Image.gif\" alt=\"Mein Bild\"/>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("!Image.gif|alt=Mein Bild!", wiki);
  }
  public void testImage1()
  {
    String html = "<img src=\"Image.gif\"/>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("!Image.gif!", wiki);
  }
  
  public void testTable3()
  {
    String html = "<table class=\"gwikiTable\"><tr><td class=\"gwikitd\">a<br/>b</td></tr></table>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("|a\\\\\nb|\n", wiki);
  }

  public void testTable2()
  {
    String html = "<table class=\"gwikiTable\"><tr><th class=\"gwikith\">asdf</td></tr><tr><td class=\"gwikitd\">x</td</td></table>";

    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("||asdf||\n|x|\n", wiki);
  }

  public void testTable1()
  {
    String html = "<table class=\"gwikiTable\"><tr><td class=\"gwikitd\">asdf</td></tr></table>";

    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("|asdf|\n", wiki);
  }

  public void testHtmlTag2()
  {
    String html = "<span style=\"x\">asdf</span>";

    String wiki = transform(html, "span");
    System.out.println(wiki);
    assertEquals("{span:style=x}asdf{span}", wiki);
  }

  public void testHtmlTag()
  {
    String html = "<strong>asdf</strong>";

    String wiki = transform(html, "strong");
    System.out.println(wiki);
    assertEquals("*asdf*", wiki);
  }

  public void testLiNestedNr()
  {
    String html = "<ul><li>Eins</li><ol><li>Nested</li></ol></ul>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("* Eins\n#* Nested\n", wiki);
  }

  public void testLiNested()
  {
    String html = "<ul><li>Eins</li><ul><li>Nested</li></ul></ul>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("* Eins\n** Nested\n", wiki);
  }

  public void NotWorkingtestLinkWithTarget2()
  {
    String html = "<a wikiTarget=\"Bla|MyTarget\" href=\"MyTarget\">Bla</a>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("[Bla|MyTarget]", wiki);
  }

  public void testLinkWithTarget1()
  {
    String html = "<a wikiTarget=\"MyTarget\" href=\"MyTarget\">Bla</a>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("[MyTarget]", wiki);
  }

  public void testNl()
  {
    String html = "a<br/>b";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("a\nb", wiki);
  }

  public void testLiNl()
  {
    String html = "<ul><li>a<br>b</li></ul>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("* a\\\\\nb\n", wiki);
  }

  public void testUnkownMacro()
  {
    String html = "{asdf}";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("{asdf}", wiki);
  }

  public void testHr()
  {
    String html = "a\n<hr/>b";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("a\n----\nb", wiki);
  }

  public void testBold()
  {
    String html = "<b>bold</b>notbold";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("*bold*notbold", wiki);
  }

  public void testUl2()
  {
    String html = "<ul><li>First</li>\n\n<li>Second</li></ul>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("* First\n* Second\n", wiki);
  }

  public void testUl()
  {
    String html = "<ul><li>First</li>\n<li>Second</li></ul>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("* First\n* Second\n", wiki);
  }

  public void testH1()
  {
    String html = "<h1>First</h1>\n<h2>Second</h2>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("h1.First\nh2.Second\n", wiki);
  }
}

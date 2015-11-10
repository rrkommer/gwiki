////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
//
////////////////////////////////////////////////////////////////////////////

// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiQuoteMacroBean;

public class Html2WikiFilterTest extends Html2WikiBase
{
  public void testMacro()
  {
    String html = "{todo:title=Ungekl&auml;rt}\nx\n{todo}";
    String wiki = transform(html);
    System.out.println(wiki);
    // assertEquals("a \\*NotBold\\* b", wiki);
  }

  public void testSpecialCharacters()
  {
    String html = "a *NotBold* b";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals("a \\*NotBold\\* b", wiki);

  }

  public void testBlockQuote()
  {
    Html2WikiFilter nf = new Html2WikiFilter();
    // nf.getSupportedHtmlTags().addAll(Html2TextFilter.getH);
    nf.getMacroTransformer().add(new GWikiQuoteMacroBean().getTransformInfo());
    nf.getSupportedHtmlTags().add("div");
    String html = "<blockquote><div class=\"gwikiContent\">bla<br/></div></blockquote>";
    String wiki = nf.transform(html);
    System.out.println(wiki);
  }

  public void testNestedLi()
  {
    String html = "<ul class=\"minus\" type=\"square\"><li>1a<ul class=\"minus\" type=\"square\"><li>2a<ul class=\"minus\" type=\"square\"><li>3a</li></ul></li><li>2b</li><li>2c</li></ul></li><li>1b</li></ul>";
    String wiki = transform(html);
    System.out.println(wiki);
  }

  public void testTable4Nested2()
  {
    String html = "<table><tr><td><table><tr><td>x</td></tr></table></td></tr></table>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals(
        "{table:class=gwikiTable}\n{tr:class=gwikitr}\n{td:class=gwikitd}\n{table:class=gwikiTable}\n{tr:class=gwikitr}\n{td:class=gwikitd}\nx\n{td}\n{tr}\n{table}\n{td}\n{tr}\n{table}",
        wiki);
  }

  public void testTable4Nested()
  {
    String html = "<table class=\"gwikiTable\"><tr><td class=\"gwikitd\"><table class=\"gwikiTable\"><tr><td class=\"gwikitd\">x</td></tr></table></td></tr></table>";
    String wiki = transform(html);
    System.out.println(wiki);
    assertEquals(
        "{table:class=gwikiTable}\n{tr:class=gwikitr}\n{td:class=gwikitd}\n{table:class=gwikiTable}\n{tr:class=gwikitr}\n{td:class=gwikitd}\nx\n{td}\n{tr}\n{table}\n{td}\n{tr}\n{table}",
        wiki);
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
    assertEquals("{*}bold{*}notbold", wiki);
    html = "<b>bold</b> notbold";
    wiki = transform(html);
    System.out.println(wiki);
    assertEquals("*bold* notbold", wiki);
    html = "x<b>bold</b> notbold";
    wiki = transform(html);
    System.out.println(wiki);
    assertEquals("x{*}bold{*} notbold", wiki);
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

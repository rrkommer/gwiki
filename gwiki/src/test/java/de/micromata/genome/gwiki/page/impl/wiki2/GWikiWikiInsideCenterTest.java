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
package de.micromata.genome.gwiki.page.impl.wiki2;

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;

/**
 * @author roger
 * 
 */
public class GWikiWikiInsideCenterTest extends GWikiWikiParserTestBase
{
  public void testEvaledBody2Test()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("center", GWikiHtmlBodyTagMacro.nestedHtmlBody());

    String wiki = "{center}bla!{center}";
    String html = "<p><center>bla!</center></p>\n";
    w2htest(wiki, html, macroFactories);
  }

  public void testEvaledBodyTest()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("center", GWikiHtmlBodyTagMacro.nestedHtmlBody());

    String wiki = "{center}bla{center}";
    String html = "<p><center>bla</center></p>\n";
    w2htest(wiki, html, macroFactories);
  }
}

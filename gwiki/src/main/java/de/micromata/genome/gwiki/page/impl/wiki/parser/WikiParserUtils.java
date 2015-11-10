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
package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.utils.html.Html2WikiFilter;

/**
 * 
 * Utils to convert Wiki to html and vice versa.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class WikiParserUtils
{
  public static List<GWikiFragment> parseText(String wikiText, String macroName, GWikiMacroFactory macroFactorie)
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put(macroName, macroFactorie);
    return parseText(wikiText, macroFactories);
  }

  public static List<GWikiFragment> parseText(String wikiText, Map<String, GWikiMacroFactory> macroFactories)
  {
    GWikiWikiParserContext ctx = new GWikiWikiParserContext();
    ctx.getMacroFactories().putAll(macroFactories);
    GWikiWikiParser parser = new GWikiWikiParser();
    parser.parseFrags(wikiText, ctx);
    List<GWikiFragment> frags = ctx.popFragList();
    return frags;
  }

  public static List<GWikiFragment> parseText(String wikiText)
  {
    Map<String, GWikiMacroFactory> macroFactories = Collections.emptyMap();
    return parseText(wikiText, macroFactories);
  }

  public static String wiki2html(String wikiText, int renderMode)
  {
    List<GWikiFragment> fr = parseText(wikiText);
    GWikiStandaloneContext ctx = new GWikiStandaloneContext();
    ctx.setRenderMode(renderMode);
    for (GWikiFragment f : fr) {
      f.render(ctx);
    }
    return ctx.getJspWriter().getString();
  }

  /**
   * Render Wiki to text.
   * 
   * @param wikiText
   * @return "" if html is null.
   */
  public static String wiki2html(String wikiText)
  {
    if (wikiText == null) {
      return "";
    }
    return wiki2html(wikiText, 0);
  }

  /**
   * Converts HTML back to wiki.
   * 
   * @param html
   * @return "" if html is null.
   */
  public static String html2wiki(String html)
  {
    if (html == null) {
      return "";
    }
    return Html2WikiFilter.html2Wiki(html);
  }

  /**
   * 
   * @param html html text.
   * @param supportedHtmlTags known html macros.
   * 
   * <pre></pre>
   * 
   *          will be translated into {pre}{pre} in wiki.
   * @return "" if html is null.
   */
  public static String html2wiki(String html, Set<String> supportedHtmlTags)
  {
    if (html == null) {
      return "";
    }
    return Html2WikiFilter.html2Wiki(html, supportedHtmlTags);
  }
}

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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildsBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
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
   *          <pre></pre>
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

  public static void dumpFragmentTree(List<GWikiFragment> frags, StringBuilder sb, String indent)
  {
    for (GWikiFragment frag : frags) {
      dumpFragmentTree(frag, sb, indent);
    }
  }

  public static void dumpFragmentTree(GWikiFragment frag, StringBuilder sb, String indent)
  {
    sb.append(indent);
    sb.append(frag.getClass().getSimpleName());
    if (frag instanceof GWikiMacroFragment) {
      GWikiMacroFragment mf = (GWikiMacroFragment) frag;
      sb.append(": ");
      mf.getAttrs().toHeadContent(sb);
      sb.append("\n");
      if (mf.getAttrs().getChildFragment() != null
          && mf.getAttrs().getChildFragment().getChilds().isEmpty() == false) {
        dumpChildFragments(mf.getAttrs().getChildFragment(), sb, indent + "  ");
      } else if (mf.getMacro().hasBody() && mf.getMacro().evalBody() == false) {
        sb.append(StringEscapeUtils.escapeJava(mf.getAttrs().getBody()));
      }
      return;
    }
    if (frag instanceof GWikiFragmentText) {
      sb.append(": ").append(groovy.json.StringEscapeUtils.escapeJava(((GWikiFragmentText) frag).getHtml()));
    }
    sb.append("\n");
    if (frag instanceof GWikiFragmentChildsBase) {
      dumpChildFragments((GWikiFragmentChildsBase) frag, sb, indent + " ");
    }

  }

  protected static void dumpChildFragments(GWikiFragmentChildsBase frag, StringBuilder sb, String ident)
  {
    dumpFragmentTree(frag.getChilds(), sb, ident);
  }
}

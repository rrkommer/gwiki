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

package de.micromata.genome.gwiki.utils.html;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.xni.XMLAttributes;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributesUtils;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Information to tranform html back to wiki syntax.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class Html2WikiTransformInfo implements Html2WikiTransformer
{
  private String tagName;

  /**
   * Name of the macro
   */
  private String macroName;

  /**
   * creates the macro
   */
  private GWikiMacroFactory macroFactory;

  /**
   * List of required macros
   */

  private Matcher<SaxElement> saxElementMatcher;

  public Html2WikiTransformInfo()
  {

  }

  public Html2WikiTransformInfo(String tagName, Matcher<SaxElement> saxElementMatcher, String macroName,
      Class<? extends GWikiMacro> macro)
  {
    this.tagName = tagName;
    this.macroName = macroName;
    this.macroFactory = new GWikiMacroClassFactory(macro);
    this.saxElementMatcher = saxElementMatcher;
  }

  public Html2WikiTransformInfo(String tagName, Matcher<SaxElement> saxElementMatcher, String macroName,
      GWikiMacroFactory macroFactory)
  {
    this.tagName = tagName;
    this.macroName = macroName;
    this.macroFactory = macroFactory;
    this.saxElementMatcher = saxElementMatcher;
  }

  @Override
  public boolean match(String tagName, XMLAttributes attributes, boolean withBody)
  {
    SaxElement saxElement = new SaxElement(tagName, attributes, withBody);
    if (saxElementMatcher.match(saxElement) == false) {
      return false;
    }

    return true;
  }

  public static String getMarcroArgsAttributesForRte(GWikiContext ctx, MacroAttributes attrs)
  {
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == false) {
      return "";
    }
    StringBuilder ret = new StringBuilder();
    StringBuilder sbsourehead = new StringBuilder();
    attrs.toHeadContent(sbsourehead);

    ret.append("  data-macroname='").append(attrs.getCmd()).append("' data-macrohead='").append(sbsourehead.toString())
        .append("'");
    if (attrs.getArgs().isEmpty() == true) {
      return ret.toString();
    }
    String maargs = MacroAttributes.encode(attrs.getArgs().getMap());
    ret.append(" title=\"").append(maargs).append("\"");
    return ret.toString();

  }

  public static void renderMacroArgs(GWikiContext ctx, MacroAttributes attrs)
  {
    String sb = getMarcroArgsAttributesForRte(ctx, attrs);
    ctx.append(sb);
  }

  @Override
  public GWikiMacroFragment handleMacroTransformer(String tagName, XMLAttributes attributes, boolean withBody)
  {
    MacroAttributes ma = new MacroAttributes();
    ma.setCmd(macroName);
    GWikiMacro macro = macroFactory.createInstance();
    GWikiMacroFragment frag = new GWikiMacroFragment(macro, ma);
    String title = attributes.getValue("title");
    if (StringUtils.isNotBlank(title) == true) {
      Map<String, String> args = MacroAttributesUtils.decode(title);
      frag.getAttrs().getArgs().getMap().putAll(args);
    }
    return frag;
  }

  @Override
  public void handleMacroEnd(String tagname, GWikiMacroFragment lpfm, List<GWikiFragment> children, String body)
  {
    // if ((lpfm instanceof GWikiBodyEvalMacro) == false) {
    lpfm.getAttrs().setBody(body);
    // } else {
    if (children != null) {
      lpfm.addChilds(children);
    }
    // }
  }

  public GWikiMacroFactory getMacroFactory()
  {
    return macroFactory;
  }

  public void setMacroFactory(GWikiMacroFactory macroFactory)
  {
    this.macroFactory = macroFactory;
  }

  public String getTagName()
  {
    return tagName;
  }

  public void setTagName(String tagName)
  {
    this.tagName = tagName;
  }

}

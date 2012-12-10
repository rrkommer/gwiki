////////////////////////////////////////////////////////////////////////////
//
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
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.utils.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.xni.XMLAttributes;

import de.micromata.genome.gwiki.page.GWikiContext;
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
  public static class AttributeMatcher
  {
    /**
     * name of the attribute
     */
    private String name;

    /**
     * should match
     */
    private Matcher<String> valueMatcher;

    public AttributeMatcher()
    {

    }

    public AttributeMatcher(String name, Matcher<String> valueMatcher)
    {
      this.name = name;
      this.valueMatcher = valueMatcher;
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public Matcher<String> getValueMatcher()
    {
      return valueMatcher;
    }

    public void setValueMatcher(Matcher<String> valueMatcher)
    {
      this.valueMatcher = valueMatcher;
    }

  }

  /**
   * Name of the html tag.
   */
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
  private List<AttributeMatcher> attributeMatcher = new ArrayList<AttributeMatcher>();

  public Html2WikiTransformInfo()
  {

  }

  public Html2WikiTransformInfo(String tagName, String macroName, Class< ? extends GWikiMacro> macro)
  {
    this.tagName = tagName;
    this.macroName = macroName;
    this.macroFactory = new GWikiMacroClassFactory(macro);
  }

  public Html2WikiTransformInfo(String tagName, String macroName, GWikiMacroFactory macroFactory, List<AttributeMatcher> attributeMatcher)
  {
    this.tagName = tagName;
    this.macroName = macroName;
    this.macroFactory = macroFactory;
    this.attributeMatcher = attributeMatcher;
  }

  protected boolean matchAttribute(AttributeMatcher am, XMLAttributes attributes)
  {
    String value = attributes.getValue(am.getName());
    return am.getValueMatcher().match(value);
  }

  public boolean match(String tagName, XMLAttributes attributes, boolean withBody)
  {
    if (getTagName().equals(tagName) == false) {
      return false;
    }
    if (macroFactory.hasBody() != withBody) {
      return false;
    }

    for (AttributeMatcher am : getAttributeMatcher()) {
      if (matchAttribute(am, attributes) == false) {
        return false;
      }
    }
    return true;
  }

  public static void renderMacroArgs(GWikiContext ctx, MacroAttributes attrs)
  {
    if (attrs.getArgs().isEmpty() == true) {
      return;
    }
    String maargs = MacroAttributes.encode(attrs.getArgs().getMap());
    ctx.append(" title=\"").append(maargs).append("\"");
  }

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

  public String getTagName()
  {
    return tagName;
  }

  public void setTagName(String tagName)
  {
    this.tagName = tagName;
  }

  public GWikiMacroFactory getMacroFactory()
  {
    return macroFactory;
  }

  public void setMacroFactory(GWikiMacroFactory macroFactory)
  {
    this.macroFactory = macroFactory;
  }

  public List<AttributeMatcher> getAttributeMatcher()
  {
    return attributeMatcher;
  }

  public void setAttributeMatcher(List<AttributeMatcher> attributeMatcher)
  {
    this.attributeMatcher = attributeMatcher;
  }

}

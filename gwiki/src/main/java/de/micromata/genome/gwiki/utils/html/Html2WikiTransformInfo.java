/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.01.2010
// Copyright Micromata 06.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.xni.XMLAttributes;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Information to tranform html back to wiki syntax.
 * 
 * @author roger@micromata.de
 * 
 */
public class Html2WikiTransformInfo
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

  public GWikiMacroFragment handleMacroTransformer(String tagName, XMLAttributes attributes, boolean withBody)
  {
    MacroAttributes ma = new MacroAttributes();
    ma.setCmd(macroName);
    GWikiMacro macro = macroFactory.createInstance();
    GWikiMacroFragment frag = new GWikiMacroFragment(macro, ma);
    return frag;
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

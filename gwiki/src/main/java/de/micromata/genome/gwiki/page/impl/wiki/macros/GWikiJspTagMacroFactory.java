/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   10.01.2010
// Copyright Micromata 10.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.TagInfo;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.utils.ClassUtils;

/**
 * Factory for a GWikiJspTagMacro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiJspTagMacroFactory implements GWikiMacroFactory
{
  private TagInfo tagInfo;

  private JspTag tag;

  public GWikiJspTagMacroFactory(TagInfo tagInfo)
  {
    super();
    this.tagInfo = tagInfo;
    this.tag = createTag();
  }

  public String toString()
  {
    return "JspTag(" + tagInfo.getTagName() + ")";
  }

  protected JspTag createTag()
  {
    String className = tagInfo.getTagClassName();
    return ClassUtils.createDefaultInstance(className, JspTag.class);
  }

  public GWikiMacro createInstance()
  {
    return new GWikiJspTagMacro(tagInfo, createTag());
  }

  public boolean evalBody()
  {
    return tag instanceof BodyTag;
    // return false;
  }

  public boolean hasBody()
  {
    return evalBody();
  }

  public boolean isRteMacro()
  {
    return false;
  }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagInfo;

import org.apache.commons.beanutils.BeanUtilsBean;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.gspt.ChildPageContext;
import de.micromata.genome.gwiki.page.gspt.GspPageContext;
import de.micromata.genome.gwiki.page.gspt.TagSupport;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Implements a Macro using a jsp tag.
 * 
 * The tag library has to be imported with the GWikiUseJspTagLibMacro.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiJspTagMacro extends GWikiMacroBase implements GWikiRuntimeMacro
{

  private static final long serialVersionUID = 4749715448005298813L;

  private JspTag tag;

  private TagInfo tagInfo;

  public GWikiJspTagMacro(TagInfo tagInfo, JspTag tag)
  {
    this.tagInfo = tagInfo;
    this.tag = tag;

  }

  public boolean renderBodyTag(BodyTag tag, MacroAttributes attrs, GWikiContext ctx)
  {
    return true;
  }

  public boolean renderNoBodyTag(Tag tag, MacroAttributes attrs, GWikiContext ctx)
  {
    return true;
  }

  public boolean renderTag(Tag tag, MacroAttributes attrs, GWikiContext ctx)
  {
    tag.setPageContext(ctx.getPageContext());
    if (tag instanceof BodyTag) {
      return renderBodyTag((BodyTag) tag, attrs, ctx);
    }

    return renderNoBodyTag(tag, attrs, ctx);
  }

  protected void populate(Object tag, MacroAttributes attrs)
  {
    try {
      BeanUtilsBean.getInstance().populate(tag, attrs.getArgs().getMap());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  protected List<Object> convertAttributesToObjectList(MacroAttributes attrs)
  {
    List<Object> ret = new ArrayList<Object>();
    for (Map.Entry<String, String> me : attrs.getArgs().getMap().entrySet()) {
      ret.add(me.getKey());
      ret.add(me.getValue());
    }
    return ret;
  }

  protected boolean initTag(MacroAttributes attrs, GWikiContext ctx, ChildPageContext childPageContext) throws Exception
  {

    List<Object> oargs = convertAttributesToObjectList(attrs);
    if (tag instanceof BodyTag) {
      return TagSupport.initTag((BodyTag) tag, oargs, childPageContext);
    } else if (tag instanceof Tag) {
      return TagSupport.initSimpleTag((Tag) tag, oargs, childPageContext);
    }
    return true;
  }

  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    try {
      PageContext pageContext = ctx.getPageContext();
      ChildPageContext childPageContext = null;
      if (pageContext instanceof ChildPageContext) {
        childPageContext = (ChildPageContext) pageContext;
      } else {
        childPageContext = new GspPageContext(pageContext);
      }
      boolean includeBody = initTag(attrs, ctx, childPageContext);
      if ((tag instanceof BodyTag) == false) {
        if (tag instanceof Tag) {
          Tag ttag = (Tag) tag;
          ttag.doEndTag();
        }
        return true;
      }
      if (includeBody == true) {
        while (true) {
          if (attrs.getChildFragment().render(ctx) == false) {
            return false;
          }
          if (TagSupport.continueAfterBody(childPageContext) == false) {
            break;
          }
        }
        TagSupport.afterBody(childPageContext);
        return TagSupport.endTag(childPageContext);
      }
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return true;

  }

  public boolean evalBody()
  {
    return hasBody();
  }

  public int getRenderModes()
  {
    return 0;
  }

  public boolean hasBody()
  {
    // TODO Auto-generated method stub
    return tag instanceof BodyTag;
  }

}

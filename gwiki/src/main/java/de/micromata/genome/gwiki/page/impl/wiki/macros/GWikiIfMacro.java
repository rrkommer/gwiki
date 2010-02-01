/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   30.12.2009
// Copyright Micromata 30.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * if macro to test against language (lang) or right.
 * 
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiIfMacro extends GWikiMacroBean implements GWikiBodyEvalMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = -4851793784009861436L;

  private String lang;

  private String right;

  private String renderMode;

  public boolean checkCondition(GWikiContext ctx, MacroAttributes attrs)
  {
    if (StringUtils.isNotEmpty(lang) == true) {
      Locale uloc = ctx.getWikiWeb().getAuthorization().getCurrentUserLocale(ctx);
      if (uloc.getLanguage().equals(lang) == true) {
        return true;
      }
      return false;
    } else if (StringUtils.isNotEmpty(right) == true) {
      if (ctx.isAllowTo(right) == true) {
        return true;
      }
      return false;
    } else if (StringUtils.isNotEmpty(renderMode) == true) {
      RenderModes rm = RenderModes.getRenderMode(renderMode);
      if (rm == null) {
        return true;
      }
      return rm.isSet(ctx.getRenderMode());
    }
    return true;
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (checkCondition(ctx, attrs) == true) {
      attrs.getChildFragment().render(ctx);
    }
    return true;
  }

  public String getLang()
  {
    return lang;
  }

  public void setLang(String lang)
  {
    this.lang = lang;
  }

  public String getRight()
  {
    return right;
  }

  public void setRight(String right)
  {
    this.right = right;
  }

  public String getRenderMode()
  {
    return renderMode;
  }

  public void setRenderMode(String renderMode)
  {
    this.renderMode = renderMode;
  }

}

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.11.2009
// Copyright Micromata 09.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import org.apache.commons.beanutils.BeanUtilsBean;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBean;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanUtils;

/**
 * Adapter from Macro to a ActionBean.
 * 
 * First the macroAttributes will be mapped to bean and than the request parameters with prefixed names.
 * 
 * never used.
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class GWikiActionBeanMacro extends GWikiMacroBase implements GWikiRuntimeMacro, ActionBean
{

  private static final long serialVersionUID = 727126974787360910L;

  protected transient GWikiContext wikiContext;

  protected MacroAttributes macroAttributes;

  // TODO gwiki ActionBeanMacros has may be bodies?
  public boolean evalBody()
  {
    return false;
  }

  public boolean hasBody()
  {
    return false;
  }

  protected void populate(MacroAttributes attrs, GWikiContext ctx)
  {
    try {
      BeanUtilsBean.getInstance().populate(this, attrs.getArgs().getMap());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

  }

  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    this.macroAttributes = attrs;
    this.wikiContext = ctx;
    populate(attrs, ctx);
    return ActionBeanUtils.perform(this);
  }

  public String getRequestPrefix()
  {
    return macroAttributes.getRequestPrefix();
  }

  public GWikiContext getWikiContext()
  {
    return wikiContext;
  }

  public void setWikiContext(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

}

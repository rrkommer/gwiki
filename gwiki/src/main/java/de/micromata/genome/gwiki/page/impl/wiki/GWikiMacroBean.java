/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   01.11.2009
// Copyright Micromata 01.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import org.apache.commons.beanutils.BeanUtilsBean;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Simple render Bean. MacroAttributes will be mapped to Bean properties.
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class GWikiMacroBean extends GWikiMacroBase implements GWikiRuntimeMacro
{

  private static final long serialVersionUID = 317825823838434281L;

  // protected transient GWikiContext wikiContext;

  private boolean populated = false;

  public GWikiMacroBean()
  {

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
    if (populated == false) {
      populated = true;
      populate(attrs, ctx);
    }
    return renderImpl(ctx, attrs);

  }

  public abstract boolean renderImpl(GWikiContext ctx, MacroAttributes attrs);

}

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import de.micromata.genome.gwiki.model.GWikiArtefaktBase;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.PropUtils;

public class GWikiDelegateToPageExecutableArtefakt extends GWikiArtefaktBase<String> implements GWikiExecutableArtefakt<String>
{
  private String pageId;

  public String getResolvePageId(final GWikiContext ctx)
  {
    return PropUtils.eval(pageId, "skin", ctx.getSkin());
  }

  public boolean renderWithParts(final GWikiContext ctx)
  {
    GWikiElement el = ctx.getWikiWeb().getElement(getResolvePageId(ctx));
    el.serve(ctx);
    return true;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

}

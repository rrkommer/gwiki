/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.10.2009
// Copyright Micromata 18.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.Collections;
import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Hard coded html fragment.
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class GWikiFragmentHtml extends GWikiFragementBase
{

  private static final long serialVersionUID = 6726490960427363801L;

  protected String html;

  public GWikiFragmentHtml()
  {

  }

  public GWikiFragmentHtml(String html)
  {
    this.html = html;
  }

  public GWikiFragmentHtml(GWikiFragmentHtml other)
  {
    this.html = other.html;
  }

  public boolean render(GWikiContext ctx)
  {
    ctx.append(getHtml());
    return true;
  }

  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    // nothing
  }

  public String getHtml()
  {
    return html;
  }

  public List<GWikiFragment> getChilds()
  {
    return Collections.emptyList();
  }

  public void setHtml(String html)
  {
    this.html = html;
  }

}

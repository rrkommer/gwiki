/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.11.2009
// Copyright Micromata 02.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiFragmentChildContainer extends GWikiFragmentChildsBase
{
  private static final long serialVersionUID = 8561771659956368069L;

  public GWikiFragmentChildContainer()
  {
    super();
  }

  public GWikiFragmentChildContainer(GWikiFragmentChildContainer other)
  {
    super(other);
  }

  public GWikiFragmentChildContainer(List<GWikiFragment> childs)
  {
    super(childs);
  }

  public boolean render(GWikiContext ctx)
  {
    if (childs == null) {
      return true;
    }
    for (GWikiFragment c : childs) {
      if (c.render(ctx) == false)
        return false;
    }
    return true;
  }

  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    if (childs == null) {
      return;
    }
    for (GWikiFragment c : childs) {
      c.ensureRight(ctx);
    }
  }

  public void getSource(StringBuilder sb)
  {
    getChildSouce(sb);
  }

}

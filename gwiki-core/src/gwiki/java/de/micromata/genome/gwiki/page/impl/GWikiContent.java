/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.10.2009
// Copyright Micromata 21.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;

// TODO gwiki use GWikiFragmentChildsBase
public class GWikiContent extends GWikiFragmentChildContainer
{

  private static final long serialVersionUID = 2245840781845976055L;

  public GWikiContent(List<GWikiFragment> fragments)
  {
    super(fragments);
  }

  public GWikiContent(GWikiContent other)
  {
    super(other);
  }

  @Override
  public Object clone()
  {
    return new GWikiContent(this);
  }

  public boolean render(GWikiContext ctx)
  {
    for (GWikiFragment f : childs) {
      if (f.render(ctx) == false)
        return false;
    }
    if (RenderModes.InMem.isSet(ctx.getRenderMode()) == false) {
      ctx.flush();
    }
    return true;
  }
}

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   28.10.2009
// Copyright Micromata 28.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Abstract implementation of a fragment with childs.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiFragmentChildsBase extends GWikiFragementBase
{

  private static final long serialVersionUID = -167135240006781273L;

  protected List<GWikiFragment> childs;

  public GWikiFragmentChildsBase()
  {
    childs = new ArrayList<GWikiFragment>();
  }

  public GWikiFragmentChildsBase(List<GWikiFragment> childs)
  {
    this.childs = childs;
  }

  public GWikiFragmentChildsBase(GWikiFragmentChildsBase other)
  {
    childs = new ArrayList<GWikiFragment>();
    childs.addAll(other.childs);
  }

  public void iterate(GWikiFragmentVisitor visitor)
  {
    visitor.begin(this);
    for (GWikiFragment c : childs) {
      c.iterate(visitor);
    }
    visitor.end(this);
  }

  public void getChildSouce(StringBuilder sb)
  {
    for (GWikiFragment c : childs) {
      c.getSource(sb);
    }
  }

  public void renderChilds(GWikiContext ctx)
  {
    for (GWikiFragment c : childs) {
      c.render(ctx);
    }
  }

  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    for (GWikiFragment c : childs) {
      c.ensureRight(ctx);
    }
  }

  public void addChilds(List<GWikiFragment> childs)
  {
    this.childs.addAll(childs);
  }

  public void addChilds(GWikiFragment child)
  {
    childs.add(child);
  }

  public void addChild(GWikiFragment child)
  {
    childs.add(child);
  }

  public List<GWikiFragment> getChilds()
  {
    return childs;
  }

}

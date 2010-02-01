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

import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;

public abstract class GWikiFragmentDecorator extends GWikiFragmentChildsBase
{

  private static final long serialVersionUID = -6329588696626151008L;

  private String prefix;

  private String suffix;

  public GWikiFragmentDecorator()
  {

  }

  public GWikiFragmentDecorator(String prefix, String suffix)
  {
    this.prefix = prefix;
    this.suffix = suffix;
  }

  public GWikiFragmentDecorator(String prefix, String suffix, List<GWikiFragment> childs)
  {
    super(childs);
    this.prefix = prefix;
    this.suffix = suffix;
  }

  public GWikiFragmentDecorator(GWikiFragmentDecorator other)
  {
    super(other);
    this.prefix = other.prefix;
    this.suffix = other.suffix;
  }

  public boolean render(GWikiContext ctx)
  {
    if (RenderModes.ForText.isSet(ctx.getRenderMode()) == false) {
      ctx.append(prefix);
    }
    renderChilds(ctx);
    if (RenderModes.ForText.isSet(ctx.getRenderMode()) == false) {
      ctx.append(suffix);
    }
    return true;
  }

  public String getPrefix()
  {
    return prefix;
  }

  public void setPrefix(String prefix)
  {
    this.prefix = prefix;
  }

  public String getSuffix()
  {
    return suffix;
  }

  public void setSuffix(String suffix)
  {
    this.suffix = suffix;
  }

}

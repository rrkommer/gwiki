/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.12.2009
// Copyright Micromata 06.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.matcher;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.MatcherBase;

/**
 * Base implementation for a GWikiElementInfo matcher.
 * 
 * @author roger
 * 
 */
public abstract class GWikiElementMatcherBase extends MatcherBase<GWikiElementInfo>
{

  private static final long serialVersionUID = -3241664191521229581L;

  protected GWikiContext wikiContext;

  public GWikiElementMatcherBase(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

  abstract public boolean match(GWikiElementInfo ei);

  public GWikiContext getWikiContext()
  {
    return wikiContext;
  }

  public void setWikiContext(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

}

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

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Matches if GWikiElement is viewable.
 * 
 * @author roger
 * 
 */
public class GWikiViewableMatcher extends GWikiElementMatcherBase
{

  private static final long serialVersionUID = 8271727742238624099L;

  GWikiAuthorization auth;

  public GWikiViewableMatcher(GWikiContext wikiContext)
  {
    super(wikiContext);
    auth = wikiContext.getWikiWeb().getAuthorization();
  }

  public boolean match(GWikiElementInfo object)
  {
    if (object == null) {
      return false;
    }
    if (auth.isAllowToView(wikiContext, object) == true) {
      return true;
    }
    return false;
  }

}

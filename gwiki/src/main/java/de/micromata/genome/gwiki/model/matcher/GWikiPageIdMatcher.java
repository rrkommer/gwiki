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
import de.micromata.genome.util.matcher.Matcher;

/**
 * Matches agains a page id matcher.
 * 
 * @author roger
 * 
 */
public class GWikiPageIdMatcher extends GWikiElementMatcherBase
{

  private static final long serialVersionUID = 8721123274206488774L;

  private Matcher<String> pageIdMatcher;

  public GWikiPageIdMatcher(GWikiContext wikiContext, Matcher<String> pageIdMatcher)
  {
    super(wikiContext);
    this.pageIdMatcher = pageIdMatcher;
  }

  @Override
  public boolean match(GWikiElementInfo ei)
  {
    return pageIdMatcher.match(ei.getId());
  }

}

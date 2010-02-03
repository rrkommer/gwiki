/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   17.12.2009
// Copyright Micromata 17.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.matcher;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.Matcher;

public class GWikiElementPropMatcher extends GWikiElementMatcherBase
{
  protected String propName;

  protected Matcher<String> propValueMatcher;

  public GWikiElementPropMatcher(GWikiContext wikiContex, String propName, Matcher<String> propValueMatcher)
  {
    super(wikiContex);
    this.propName = propName;
    this.propValueMatcher = propValueMatcher;

  }

  @Override
  public boolean match(GWikiElementInfo ei)
  {
    return propValueMatcher.match(ei.getProps().getStringValue(propName));
  }

}

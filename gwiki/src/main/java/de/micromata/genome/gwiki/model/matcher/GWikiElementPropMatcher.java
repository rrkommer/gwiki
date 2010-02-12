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

/**
 * Matcher to match agains a property inside a properties file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementPropMatcher extends GWikiElementMatcherBase
{

  private static final long serialVersionUID = 4885823399615447244L;

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

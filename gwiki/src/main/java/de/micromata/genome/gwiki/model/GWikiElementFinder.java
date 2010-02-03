/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   17.12.2009
// Copyright Micromata 17.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.matcher.GWikiElementPropMatcher;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.model.matcher.GWikiViewableMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.AndMatcher;
import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Helper to find Elements.
 * 
 * The common way to access this is wikiContext.getElementFinder().
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiElementFinder
{
  protected GWikiContext wikiContext;

  public GWikiElementFinder(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;

  }

  public Matcher<GWikiElementInfo> byPageIdName(Matcher<String> matcher)
  {
    return new GWikiPageIdMatcher(wikiContext, matcher);
  }

  public List<GWikiElementInfo> getPageInfos(Matcher<GWikiElementInfo> matcher)
  {
    List<GWikiElementInfo> ret = new ArrayList<GWikiElementInfo>();

    for (GWikiElementInfo ei : wikiContext.getWikiWeb().getPageInfos().values()) {
      if (matcher.match(ei) == true) {
        ret.add(ei);
      }
    }
    return ret;
  }

  public List<GWikiElementInfo> getPageAttachments(String pageId)
  {
    List<GWikiElementInfo> childs = getPageInfos(//
    new AndMatcher<GWikiElementInfo>(//
        new GWikiViewableMatcher(wikiContext), new AndMatcher<GWikiElementInfo>(//
            new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, new EqualsMatcher<String>(pageId)),//
            new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.TYPE, new EqualsMatcher<String>("attachment")))));
    return childs;
  }

  public List<GWikiElementInfo> getPageDirectPages(String pageId)
  {
    List<GWikiElementInfo> childs = getPageInfos(//
    new AndMatcher<GWikiElementInfo>(//
        new GWikiViewableMatcher(wikiContext), new AndMatcher<GWikiElementInfo>(//
            new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, new EqualsMatcher<String>(pageId)),//
            new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.TYPE, new EqualsMatcher<String>("gwiki")))));
    return childs;
  }

  public List<GWikiElementInfo> getDirectChilds(GWikiElementInfo ei)
  {
    String pageId = ei.getId();
    List<GWikiElementInfo> childs = getPageInfos(//
    new AndMatcher<GWikiElementInfo>(//
        new GWikiViewableMatcher(wikiContext), //
        new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, new EqualsMatcher<String>(pageId))//
    )//
    );
    return childs;
  }
}

////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

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
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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

  public List<GWikiElementInfo> getAllDirectChilds(GWikiElementInfo ei)
  {
    String pageId = ei.getId();
    List<GWikiElementInfo> childs = getPageInfos(//
    new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, new EqualsMatcher<String>(pageId))//
    );
    return childs;
  }
}

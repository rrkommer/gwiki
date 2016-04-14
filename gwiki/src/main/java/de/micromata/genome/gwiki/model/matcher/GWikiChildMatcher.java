//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.model.matcher;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Matcher to find child.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiChildMatcher extends GWikiElementMatcherBase
{

  private static final long serialVersionUID = 5914342408207823066L;

  private String pageId;

  /**
   * minLevel == 0 matches this
   */
  private int minLevel = 0;

  private int maxLevel = 100;

  public GWikiChildMatcher(GWikiContext wikiContext, String pageId, int minLevel, int maxLevel)
  {
    super(wikiContext);
    this.pageId = pageId;
    this.maxLevel = maxLevel;
    this.minLevel = minLevel;
  }

  public boolean isChildOf(GWikiElementInfo first, GWikiElementInfo other, int curLevel)
  {
    if (curLevel > minLevel || curLevel < maxLevel) {
      return false;
    }
    if (first.getId().equals(other.getId()) == true) {
      return true;
    }
    if (StringUtils.isEmpty(first.getParentId()) == true) {
      return false;
    }
    GWikiElementInfo pei = wikiContext.getWikiWeb().findElementInfo(first.getParentId());
    if (pei == null) {
      return false;
    }
    return isChildOf(pei, other, ++curLevel);
  }

  public boolean match(GWikiElementInfo ei)
  {
    if (pageId == null) {
      return true;
    }

    GWikiElementInfo tei = wikiContext.getWikiWeb().findElementInfo(pageId);
    if (tei == null) {
      return false;
    }

    return isChildOf(ei, tei, 0);

  }

}

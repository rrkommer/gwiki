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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Comparator;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementByI18NPropsComparator extends GWikiElementByPropComparator
{

  private GWikiContext wikiContext;

  public GWikiElementByI18NPropsComparator(GWikiContext wikiContext, String propName, Comparator<GWikiElementInfo> parentComparator)
  {
    super(propName, parentComparator);
    this.wikiContext = wikiContext;
  }

  public GWikiElementByI18NPropsComparator(GWikiContext wikiContext, String propName, String defaultValue)
  {
    super(propName, defaultValue);
    this.wikiContext = wikiContext;
  }

  public GWikiElementByI18NPropsComparator(GWikiContext wikiContext, String propName)
  {
    super(propName);
    this.wikiContext = wikiContext;
  }

  @Override
  public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    String s1 = wikiContext.getWikiWeb().getI18nProvider().translateProp(wikiContext, o1.getProps().getStringValue(propName, defaultValue));
    String s2 = wikiContext.getWikiWeb().getI18nProvider().translateProp(wikiContext, o2.getProps().getStringValue(propName, defaultValue));
    if (s1 == s2)
      return compareParent(o1, o2);
    if (s1 == null)
      return 1;
    if (s2 == null)
      return -1;
    int ret = s1.compareTo(s2);
    if (ret != 0)
      return ret;
    return compareParent(o1, o2);
  }

}

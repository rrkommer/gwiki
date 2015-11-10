////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Comparator;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * Comparator to compare to GWiki Element info by order property value.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementByOrderComparator extends GWikiElementComparatorBase
{
  public GWikiElementByOrderComparator()
  {
  }

  public GWikiElementByOrderComparator(Comparator<GWikiElementInfo> parentComparator)
  {
    super(parentComparator);
  }

  public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    int i0 = o1.getOrder();
    int i1 = o2.getOrder();
    if (i1 == i0)
      return compareParent(o1, o2);
    if (i1 == -1) {
      return -1;
    }
    if (i0 == -1)
      return 1;
    if (i1 < i0)
      return 1;
    if (i1 > i0)
      return -1;
    return compareParent(o1, o2);
  }

}

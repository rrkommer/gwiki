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
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Compares elements by String list in prop CHILDORDER in the parent element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementByChildOrderComparator extends GWikiElementComparatorBase
{

  public GWikiElementByChildOrderComparator()
  {
    super();
  }

  public GWikiElementByChildOrderComparator(Comparator<GWikiElementInfo> parentComparator)
  {
    super(parentComparator);
  }

  public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    if (ctx == null) {
      return compareParent(o1, o2);
    }
    GWikiElementInfo inf1 = o1.getParent(ctx);
    GWikiElementInfo inf2 = o2.getParent(ctx);

    if (inf1 == null && inf2 == null) {
      return 0;
    }

    if (inf1 != inf2) {
      return compareParent(o1, o2);
    }
    List<String> s = inf1.getProps().getStringList(GWikiPropKeys.CHILDORDER);
    if (s == null || s.isEmpty() == true) {
      return compareParent(o1, o2);
    }
    int idx1 = s.indexOf(o1.getId());
    int idx2 = s.indexOf(o2.getId());
    if (idx1 == idx2) {
      return compareParent(o1, o2);
    }
    if (idx1 == -1) {
      return 1;
    }
    if (idx2 == -1) {
      return -1;
    }
    return idx1 - idx2;
  }

}

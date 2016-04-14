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

package de.micromata.genome.gwiki.plugin.keywordsmarttags_1_0;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

public class KeyWordRanges extends ArrayList<KeyWordRange>
{

  private static final long serialVersionUID = -8223981452656169961L;

  protected boolean intersect(int s1, int e1, int s2, int e2)
  {
    if (e2 <= s1) {
      return false;
    }
    if (e1 <= s2) {
      return false;
    }
    return true;
  }

  public void addIntersect(int start, int end, List<GWikiElementInfo> links)
  {
    for (int i = 0; i < size(); ++i) {
      KeyWordRange r = get(i);
      if (intersect(start, end, r.start, r.end) == false) {
        //
        continue;
      } else {
        int ns = Math.min(start, r.start);
        int ne = Math.max(end, r.end);
        List<GWikiElementInfo> ml = new ArrayList<GWikiElementInfo>();
        ml.addAll(r.links);
        ml.addAll(links);
        set(i, new KeyWordRange(ns, ne, ml));
        return;
      }
    }
    add(new KeyWordRange(start, end, links));
  }
}

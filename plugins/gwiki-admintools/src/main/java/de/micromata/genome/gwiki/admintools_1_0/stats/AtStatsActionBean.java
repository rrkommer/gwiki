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

package de.micromata.genome.gwiki.admintools_1_0.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.wiki.filter.GWikiUseCounterFilter;

/**
 * @author roger
 * 
 */
public class AtStatsActionBean extends ActionBeanBase
{
  private String sort;

  public Object onInit()
  {
    return null;
  }

  public Object onRefresh()
  {

    return onInit();
  }

  public void render()
  {
    Map<String, Integer> m = GWikiUseCounterFilter.getUseCounters();
    List<Map.Entry<String, Integer>> l = new ArrayList<Map.Entry<String, Integer>>();
    l.addAll(m.entrySet());
    boolean byName = false;
    Comparator<Entry<String, Integer>> byNameComparator = new Comparator<Map.Entry<String, Integer>>() {

      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
      {
        return o1.getKey().compareTo(o2.getKey());
      }

    };
    Comparator<Entry<String, Integer>> byCount = new Comparator<Map.Entry<String, Integer>>() {

      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
      {
        return o2.getValue().compareTo(o1.getValue());
      }

    };
    if (byName == true) {
      Collections.sort(l, byNameComparator);
    } else {
      Collections.sort(l, byCount);
    }
    String borderstyle = "border=\"1px\" style=\"color:black; border-style: solid;\"";
    print("<table cellspacing=\"0\" cellpadding=\"3\"" + borderstyle + ">\n");

    for (Map.Entry<String, Integer> me : l) {
      print(" <tr><td " + borderstyle + ">").print(me.getValue())//
          .print("</td><td " + borderstyle + ">").print(esc(me.getKey())).print("</td></tr>\n");
    }
    print("</table>\n");
  }
}

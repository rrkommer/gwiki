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
package de.micromata.genome.gwiki.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import junit.framework.TestCase;
import de.micromata.genome.gwiki.GWikiTestBuilder;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class CalendarControlTest extends TestCase
{
  protected String renderCalendar(List<List<Integer>> cm)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("|SO|MO|DI|MI|DO|FR|SA|\n");
    for (List<Integer> r : cm) {
      for (Integer i : r) {
        sb.append("|");
        if (i == null) {
          sb.append("  ");
        } else {
          if (i < 10) {
            sb.append(" ");
          }
          sb.append(i);
        }
      }
      sb.append("|\n");
    }
    return sb.toString();
  }

  public void testCalendar()
  {
    TimeZone tz = TimeZone.getDefault();
    Date d = new Date();
    List<List<Integer>> cm = CalendarControl.getCalTableFromMonth(d, tz);
    System.out.println(d);
    System.out.println(renderCalendar(cm));
    d = new Date(d.getTime() + TimeInMillis.WEEK * 5);
    cm = CalendarControl.getCalTableFromMonth(d, tz);
    System.out.println(d);
    System.out.println(renderCalendar(cm));

    d = new Date(d.getTime() - TimeInMillis.WEEK * 12);
    cm = CalendarControl.getCalTableFromMonth(d, tz);
    System.out.println(d);
    System.out.println(renderCalendar(cm));
  }

  public void testCalendarR()
  {
    GWikiTestBuilder tb = new GWikiTestBuilder();
    GWikiContext wikiContext = tb.createWikiContext();
    TimeZone tz = TimeZone.getDefault();
    List<Date> dates = new ArrayList<Date>();
    dates.add(new Date());
    CalendarControl cc = new CalendarControl(tz, new Date(), dates);
    String ret = cc.renderCalendar(wikiContext);
    System.out.println(ret);
  }
}

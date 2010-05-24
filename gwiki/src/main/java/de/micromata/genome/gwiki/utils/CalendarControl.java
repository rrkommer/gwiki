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
package de.micromata.genome.gwiki.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import de.micromata.genome.gwiki.model.GWikiI18nProvider;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Renders a calendar contol.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class CalendarControl
{
  private Date selectedDate;

  private List<Date> dates;

  private TimeZone timeZone;

  public CalendarControl(TimeZone timeZone, Date selectedDate, List<Date> dates)
  {
    super();
    this.timeZone = timeZone;
    this.selectedDate = selectedDate;
    this.dates = dates;

  }

  public static Date toFullDay(Date date, TimeZone timeZone)
  {
    if (date == null) {
      return null;
    }
    Calendar cal = GregorianCalendar.getInstance(timeZone);
    cal.setTime(date);
    cal.set(Calendar.HOUR, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.AM_PM, Calendar.AM);
    Date ret = cal.getTime();
    return ret;
  }

  public static Date toNextMonth(Date date, TimeZone timeZone)
  {
    return incrMonth(date, timeZone, 1);
  }

  public static Date toPrevMonth(Date date, TimeZone timeZone)
  {
    return incrMonth(date, timeZone, -1);
  }

  public static Date incrMonth(Date date, TimeZone timeZone, int offset)
  {
    if (date == null) {
      return null;
    }
    date = toFullDay(date, timeZone);
    Calendar cal = GregorianCalendar.getInstance(timeZone);
    cal.setTime(date);
    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + offset);
    return cal.getTime();
  }

  protected void normalizeDates()
  {
    if (selectedDate == null) {
      if (dates.size() > 0) {
        selectedDate = dates.get(0);
      } else {
        selectedDate = new Date();
      }
    }
    selectedDate = toFullDay(selectedDate, timeZone);
    for (int i = 0; i < dates.size(); ++i) {
      dates.set(i, toFullDay(dates.get(i), timeZone));
    }
  }

  /**
   * Array of List from SO until SA
   * 
   * @param date
   * @return
   */
  public static List<List<Integer>> getCalTableFromMonth(Date date, TimeZone timeZone)
  {
    List<List<Integer>> ret = new ArrayList<List<Integer>>();
    Calendar cal = GregorianCalendar.getInstance(timeZone);
    cal.setTime(date);
    // int y = cal.get(Calendar.YEAR);
    int m = cal.get(Calendar.MONTH);
    int md = 1;
    cal.set(Calendar.DAY_OF_MONTH, md);
    List<Integer> curLine = new ArrayList<Integer>();
    while (cal.get(Calendar.MONTH) == m) {
      int wd = cal.get(Calendar.DAY_OF_WEEK);
      if (wd == 1 && curLine.size() > 0) {
        ret.add(curLine);
        curLine = new ArrayList<Integer>();
      }
      for (int i = curLine.size() + 1; i < wd; ++i) {
        curLine.add(null);
      }
      curLine.add(md);
      cal.set(Calendar.DAY_OF_MONTH, ++md);
    }
    for (int i = curLine.size() + 1; i < 8; ++i) {
      curLine.add(null);
    }
    ret.add(curLine);
    return ret;
  }

  public void renderUnmatchedDay(StringBuilder sb, GWikiContext wikiContext, Date date, int monthDay)
  {
    sb.append(monthDay);
  }

  public void renderMatchedDay(StringBuilder sb, GWikiContext wikiContext, Date date, int monthDay)
  {
    sb.append("<b>").append(monthDay).append("</b>");
  }

  public void renderTableHead(StringBuilder sb, GWikiContext wikiContext, Date monthDate)
  {
    GWikiI18nProvider ip = wikiContext.getWikiWeb().getI18nProvider();
    sb.append("<tr><th>").append(ip.translate(wikiContext, "gwiki.blog.cal.SO")) //
        .append("</th><th>").append(ip.translate(wikiContext, "gwiki.blog.cal.MO")) //
        .append("</th><th>").append(ip.translate(wikiContext, "gwiki.blog.cal.DI")) //
        .append("</th><th>").append(ip.translate(wikiContext, "gwiki.blog.cal.MI")) //
        .append("</th><th>").append(ip.translate(wikiContext, "gwiki.blog.cal.DO")) //
        .append("</th><th>").append(ip.translate(wikiContext, "gwiki.blog.cal.FR")) //
        .append("</th><th>").append(ip.translate(wikiContext, "gwiki.blog.cal.SA")) //
        .append("</th></tr>\n");
  }

  public String renderCalendar(GWikiContext wikiContext)
  {
    normalizeDates();
    StringBuilder sb = new StringBuilder();

    sb.append("<table class=\"gwikiBlogCalendar\">");
    renderTableHead(sb, wikiContext, selectedDate);
    List<List<Integer>> ct = getCalTableFromMonth(selectedDate, timeZone);
    Calendar cal = GregorianCalendar.getInstance(timeZone);
    cal.setTime(selectedDate);

    for (List<Integer> wl : ct) {
      sb.append("<tr>");
      for (Integer d : wl) {
        sb.append("<td>");
        if (d == null) {
          sb.append("&nbsp;");
        } else {
          cal.set(Calendar.DAY_OF_MONTH, d);
          Date curd = cal.getTime();
          if (dates.contains(curd) == true) {
            renderMatchedDay(sb, wikiContext, curd, d);
          } else {
            renderUnmatchedDay(sb, wikiContext, curd, d);

          }
        }
        sb.append("</td>");
      }
      sb.append("</tr>\n");
    }
    sb.append("</table>");
    return sb.toString();
  }
}

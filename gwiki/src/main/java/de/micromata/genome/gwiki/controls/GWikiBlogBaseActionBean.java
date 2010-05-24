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
package de.micromata.genome.gwiki.controls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.commons.collections15.comparators.ReverseComparator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByPropComparator;
import de.micromata.genome.gwiki.utils.CalendarControl;
import de.micromata.genome.util.types.Converter;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiBlogBaseActionBean extends ActionBeanBase
{
  public static final String dayHeadFormatPattern = "E, yyyy-MM-dd";

  public static final String monthHeadFormatPattern = "yyyy-MM";

  public static final ThreadLocal<SimpleDateFormat> dayHeadReqFormat = new ThreadLocal<SimpleDateFormat>() {

    @Override
    protected SimpleDateFormat initialValue()
    {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

  };

  protected TimeZone userTimeZone;

  protected Locale userLocale;

  protected String blogPageId;

  protected GWikiElementInfo blogPage;

  protected Date selectedMonthDate = null;

  protected List<GWikiElementInfo> blogEntries = new ArrayList<GWikiElementInfo>();

  protected boolean init()
  {
    userLocale = wikiContext.getWikiWeb().getAuthorization().getCurrentUserLocale(wikiContext);
    userTimeZone = wikiContext.getUserTimeZone();
    if (StringUtils.isEmpty(blogPageId) == true) {
      blogPageId = wikiContext.getCurrentElement().getElementInfo().getId();
    }
    blogPage = wikiContext.getWikiWeb().findElementInfo(blogPageId);
    if (blogPage == null) {
      // TODO return error;
      return false;
    }
    blogEntries = wikiContext.getElementFinder().getPageDirectPages(blogPageId);
    Collections.sort(blogEntries, new ReverseComparator<GWikiElementInfo>(new GWikiElementByPropComparator("CREATEDAT")));
    return true;
  }

  protected final boolean sameMonth(Date f, Date s)
  {
    Calendar fc = GregorianCalendar.getInstance(userTimeZone);
    fc.setTime(f);
    int fy = fc.get(Calendar.YEAR);
    int fm = fc.get(Calendar.MONTH);

    fc.setTime(s);
    int sy = fc.get(Calendar.YEAR);
    int sm = fc.get(Calendar.MONTH);
    return fy == sy && fm == sm;
  }

  protected final boolean sameDay(Date f, Date s)
  {
    Calendar fc = GregorianCalendar.getInstance(userTimeZone);
    fc.setTime(f);
    int fy = fc.get(Calendar.YEAR);
    int fm = fc.get(Calendar.MONTH);
    int fd = fc.get(Calendar.DAY_OF_MONTH);
    fc.setTime(s);
    int sy = fc.get(Calendar.YEAR);
    int sm = fc.get(Calendar.MONTH);
    int sd = fc.get(Calendar.DAY_OF_MONTH);
    return fy == sy && fm == sm && fd == sd;
  }

  protected Set<String> getBlogCategories()
  {

    Set<String> set = new TreeSet<String>();
    // if (blogPage != null) {
    // List<String> bcats = blogPage.getProps().getStringList("BLOG_CATS");
    // set.addAll(bcats);
    // }
    for (GWikiElementInfo ei : blogEntries) {
      List<String> bcats = ei.getProps().getStringList("BLOG_CATS");
      set.addAll(bcats);
    }
    return set;
  }

  public void renderBlogCatHeader()
  {
    Set<String> cats = getBlogCategories();
    if (cats.isEmpty() == false) {
      String thisPl = wikiContext.localUrl(this.blogPageId);
      wikiContext.append("<div class=\"blogNavCats\">");
      wikiContext.append("<a href=\"" + thisPl + "?blogCategory=\">").append(esc(translate("gwiki.blog.page.allCats"))).append("</a>");
      for (String cat : cats) {
        wikiContext.append("&nbsp;|&nbsp;");
        wikiContext.append("<a href=\""
            + thisPl
            + "?blogCategory="
            + Converter.encodeUrlParam(cat)
            + "\">"
            + StringEscapeUtils.escapeHtml(cat)
            + "</a>");
      }
      wikiContext.append("</div>\n");
    }
  }

  public void renderCalendar()
  {
    List<Date> dates = new ArrayList<Date>(blogEntries.size());
    for (GWikiElementInfo ei : blogEntries) {
      dates.add(ei.getCreatedAt());
    }

    CalendarControl cc = new CalendarControl(wikiContext.getUserTimeZone(), selectedMonthDate, dates) {

      @Override
      public void renderMatchedDay(StringBuilder sb, GWikiContext wikiContext, Date date, int monthDay)
      {
        sb.append("<a href=\"").append(wikiContext.localUrl(blogPageId)).append("?selectedDay=")
            .append(dayHeadReqFormat.get().format(date)).append("\"><b>").append(monthDay).append("</b></a>");

      }

      @Override
      public void renderTableHead(StringBuilder sb, GWikiContext wikiContext, Date monthDate)
      {
        String mds = new SimpleDateFormat(monthHeadFormatPattern, userLocale).format(monthDate);
        String thism = dayHeadReqFormat.get().format(CalendarControl.toFullDay(new Date(), userTimeZone));
        String pid = wikiContext.localUrl(blogPageId);
        sb.append("<a href=\"").append(pid).append("\">").append(translateEsc("gwiki.blog.page.allDays")).append("</a><br/>\n");
        sb.append("<a href=\"").append(pid)//
            .append("?selectedMonth=")//
            .append(thism).append("\">").append(translateEsc("gwiki.blog.page.thisMonth")).append("</a>");
        sb.append("<tr><td colspan=\"2\">") //
            .append("<a href=\"").append(pid).append("?selectedMonth=").append(
                dayHeadReqFormat.get().format(CalendarControl.toPrevMonth(monthDate, userTimeZone))).append("\">&lt;&lt;</a>")//
            .append("</td><th colspan=\"3\"><b>") //
            .append(mds).append("</b>")//
            .append("</th><td colspan=\"2\" align=\"right\">") //
            .append("<a href=\"").append(pid).append("?selectedMonth=").append(
                dayHeadReqFormat.get().format(CalendarControl.toNextMonth(monthDate, userTimeZone))).append("\">&gt;&gt;</a>")//
            .append("</td></tr>\n");
        super.renderTableHead(sb, wikiContext, monthDate);
      }

    };
    wikiContext.append(cc.renderCalendar(wikiContext));
  }

}

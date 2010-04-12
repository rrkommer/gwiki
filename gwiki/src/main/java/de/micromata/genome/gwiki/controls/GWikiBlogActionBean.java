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
import java.util.TimeZone;

import org.apache.commons.collections15.comparators.ReverseComparator;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAttachment;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByPropComparator;
import de.micromata.genome.gwiki.utils.CalendarControl;

/**
 * TODO MO - SO I18N
 * 
 * TODO I18N Monate
 * 
 * TODO Montag - Sontag I18N
 * 
 * TODO Calendar blaettern
 * 
 * TODO falls zu viele Eintrage, blaettern
 * 
 * TODO Categorien
 * 
 * TODO Atom
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiBlogActionBean extends ActionBeanBase
{
  private String category;

  private boolean rss;

  private String selectedDay = null;

  private Date selectedDayDate = null;

  private String selectedMonth = null;

  private Date selectedMonthDate = null;

  private String parentPageId;

  private GWikiElementInfo parentPage;

  private List<GWikiElementInfo> blogEntries = new ArrayList<GWikiElementInfo>();

  private List<GWikiElementInfo> shownBlogEntries = new ArrayList<GWikiElementInfo>();

  private TimeZone userTimeZone;

  private Locale userLocale;

  // public static final ThreadLocal<SimpleDateFormat> dayHeadDisplayFormat = new ThreadLocal<SimpleDateFormat>() {
  //
  // @Override
  // protected SimpleDateFormat initialValue()
  // {
  // return new SimpleDateFormat("E, yyyy-MM-dd");
  // }
  //
  // };
  public static final String dayHeadFormatPattern = "E, yyyy-MM-dd";

  public static final String monthHeadFormatPattern = "M yyyy-MM";

  public static final ThreadLocal<SimpleDateFormat> dayHeadReqFormat = new ThreadLocal<SimpleDateFormat>() {

    @Override
    protected SimpleDateFormat initialValue()
    {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

  };

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

  protected boolean init()
  {
    userLocale = wikiContext.getWikiWeb().getAuthorization().getCurrentUserLocale(wikiContext);
    userTimeZone = wikiContext.getUserTimeZone();
    if (StringUtils.isEmpty(parentPageId) == true) {
      parentPageId = wikiContext.getCurrentElement().getElementInfo().getId();
    }
    parentPage = wikiContext.getWikiWeb().findElementInfo(parentPageId);
    if (parentPage == null) {
      // TODO return error;
      return false;
    }
    if (StringUtils.isNotBlank(selectedMonth) == true) {
      try {
        selectedMonthDate = dayHeadReqFormat.get().parse(selectedMonth);
      } catch (Exception ex) {
        // nothing
      }
    }
    if (StringUtils.isNotBlank(selectedDay) == true) {
      try {
        selectedDayDate = dayHeadReqFormat.get().parse(selectedDay);
      } catch (Exception ex) {
        // nothing
      }
    }
    if (selectedMonthDate == null) {
      if (selectedDayDate != null) {
        selectedMonthDate = selectedDayDate;
      } else {
        selectedMonthDate = new Date();
      }
    }
    blogEntries = wikiContext.getElementFinder().getPageDirectPages(parentPageId);
    Collections.sort(blogEntries, new ReverseComparator<GWikiElementInfo>(new GWikiElementByPropComparator("CREATEDAT")));

    for (GWikiElementInfo be : blogEntries) {
      if (selectedDayDate != null) {
        if (sameDay(be.getCreatedAt(), selectedDayDate) == false) {
          continue;
        }
      } else {
        if (sameMonth(be.getCreatedAt(), selectedMonthDate) == false) {
          continue;
        }
      }
      shownBlogEntries.add(be);
    }
    return true;
  }

  protected void deliverRss()
  {
    // <?xml version="1.0"?>
    // <rss version="2.0">
    // <channel>
    // <title>Liftoff News</title>
    // <link>http://liftoff.msfc.nasa.gov/</link>
    // <description>Liftoff to Space Exploration.</description>
    // <language>en-us</language>
    // <pubDate>Tue, 10 Jun 2003 04:00:00 GMT</pubDate>
    //
    // <lastBuildDate>Tue, 10 Jun 2003 09:41:01 GMT</lastBuildDate>
    // <docs>http://blogs.law.harvard.edu/tech/rss</docs>
    // <generator>Weblog Editor 2.0</generator>
    // <managingEditor>editor@example.com</managingEditor>
    // <webMaster>webmaster@example.com</webMaster>
    // <item>
    //
    // <title>Star City</title>
    // <link>http://liftoff.msfc.nasa.gov/news/2003/news-starcity.asp</link>
    // <description>How do Americans get ready to work with Russians aboard the International Space Station? They take a crash course in
    // culture, language and protocol at Russia's &lt;a href="http://howe.iki.rssi.ru/GCTC/gctc_e.htm"&gt;Star City&lt;/a&gt;.</description>
    // <pubDate>Tue, 03 Jun 2003 09:39:21 GMT</pubDate>
    // <guid>http://liftoff.msfc.nasa.gov/2003/06/03.html#item573</guid>
    //
    // </item>
    //         
    // </channel>
    // </rss>
    wikiContext.getResponse().setContentType("text/xml");
    wikiContext.append("<?xml version=\"1.0\"?>\n<rss version=\"2.0\">\n").append("<channel>\n") //
        .append("<title>" + StringEscapeUtils.escapeXml(parentPage.getTitle()) + "</title>\n") //
        .append("<link>" + wikiContext.getWikiWeb().getWikiConfig().getPublicURL() + parentPage.getId() + "</link>\n") //

    ;
    // <description>Liftoff to Space Exploration.</description>
    // <language>en-us</language>
    // <pubDate>Tue, 10 Jun 2003 04:00:00 GMT</pubDate>
    //
    // <lastBuildDate>Tue, 10 Jun 2003 09:41:01 GMT</lastBuildDate>
    // <docs>http://blogs.law.harvard.edu/tech/rss</docs>
    // <generator>Weblog Editor 2.0</generator>
    // <managingEditor>editor@example.com</managingEditor>
    // <webMaster>webmaster@example.com</webMaster>
    for (GWikiElementInfo ei : blogEntries) {
      wikiContext.append("<item>\n")//
          .append("<title>" + StringEscapeUtils.escapeXml(ei.getTitle()) + "</title>\n") //
          .append("<link>" + wikiContext.getWikiWeb().getWikiConfig().getPublicURL() + ei.getId() + "</link>\n") //
          .append("</item>\n") //
      ;
    }
    wikiContext.append("</channel>\n</rss>\n");
    wikiContext.flush();
  }

  @Override
  public Object onInit()
  {
    if (init() == false) {
      return null;
    }
    if (rss == true) {
      deliverRss();
      return noForward();
    }
    return super.onInit();
  }

  protected final String esc(String text)
  {
    return StringEscapeUtils.escapeHtml(text);
  }

  protected void renderBlockEntry(GWikiElement el, GWikiExecutableArtefakt< ? > exec)
  {
    wikiContext//
        .append("<hr><table width=\"100%\" border=\"0\"><tr><td valign=\"top\">\n") //
        .append("<h2><a href=\"").append(wikiContext.localUrl(el.getElementInfo().getId())).append("\">") //
        .append(esc(el.getElementInfo().getTitle())) //
        .append("</a></h2></td><td align=\"right\"><small>Last changed:")//
        .append(wikiContext.getUserDateString(el.getElementInfo().getModifiedAt()))//
        .append(" by ").append(esc(el.getElementInfo().getModifiedBy())) //
        .append("</small></td></tr></table>\n");

    ;

    exec.render(wikiContext);
  }

  public void renderBlogs()
  {
    wikiContext.append("Blogs here");
    String part = "MainPage";
    Date lastDay = null;
    SimpleDateFormat dayFormat = new SimpleDateFormat(dayHeadFormatPattern, userLocale);
    for (GWikiElementInfo ei : shownBlogEntries) {
      GWikiElement el = wikiContext.getWikiWeb().findElement(ei.getId());
      if (ei.isViewable() == false) {
        continue;
      }
      GWikiArtefakt< ? > art = null;
      if (StringUtils.isNotEmpty(part) == true) {
        art = el.getPart(part);
      } else {
        art = el.getMainPart();
      }
      if ((art instanceof GWikiExecutableArtefakt< ? >) == false || art instanceof GWikiAttachment) {
        continue;
      }
      Date cday = CalendarControl.toFullDay(el.getElementInfo().getCreatedAt(), userTimeZone);

      if (ObjectUtils.equals(lastDay, cday) == false) {
        wikiContext.append("<h1 class=\"BlogDayHead\">").append(esc(dayFormat.format(cday))).append("</h1>");
      }
      lastDay = cday;
      GWikiExecutableArtefakt< ? > exec = (GWikiExecutableArtefakt< ? >) art;
      renderBlockEntry(el, exec);
      GWikiContext.setCurrent(wikiContext);
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
        sb.append("<a href=\"").append(wikiContext.localUrl(parentPageId)).append("?selectedDay=").append(
            dayHeadReqFormat.get().format(date)).append("\"><b>").append(monthDay).append("</b></a>");

      }

      @Override
      public void renderTableHead(StringBuilder sb, GWikiContext wikiContext, Date monthDate)
      {
        String mds = new SimpleDateFormat(monthHeadFormatPattern, userLocale).format(monthDate);
        // TODO bleattern Monat.
        sb.append("<tr><td>&nbsp;</td><th colspan=\"5\">") //
            .append("<a href=\"")//
            .append(wikiContext.localUrl(parentPageId))//
            .append("?selectedMonth=")//
            .append(dayHeadReqFormat.get().format(monthDate)).append("\"><b>").append(mds).append("</b></a>")//
            .append("</th><td>&nbsp;</td></tr>\n");
        super.renderTableHead(sb, wikiContext, monthDate);
      }

    };
    wikiContext.append(cc.renderCalendar(wikiContext));
  }

  public String getCategory()
  {
    return category;
  }

  public void setCategory(String category)
  {
    this.category = category;
  }

  public boolean isRss()
  {
    return rss;
  }

  public void setRss(boolean rss)
  {
    this.rss = rss;
  }

  public String getSelectedDay()
  {
    return selectedDay;
  }

  public void setSelectedDay(String selectedDay)
  {
    this.selectedDay = selectedDay;
  }

  public String getSelectedMonth()
  {
    return selectedMonth;
  }

  public void setSelectedMonth(String selectedMonth)
  {
    this.selectedMonth = selectedMonth;
  }

  public String getParentPageId()
  {
    return parentPageId;
  }

  public void setParentPageId(String parentPageId)
  {
    this.parentPageId = parentPageId;
  }

}

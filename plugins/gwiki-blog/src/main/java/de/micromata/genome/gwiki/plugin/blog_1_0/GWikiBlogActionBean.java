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

package de.micromata.genome.gwiki.plugin.blog_1_0;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAttachment;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiRequestAttributeKeys;
import de.micromata.genome.gwiki.utils.CalendarControl;
import de.micromata.genome.util.types.Converter;

/**
 * TODO MO - SO I18N
 * 
 * TODO I18N Monate
 * 
 * TODO Montag - Sontag I18N
 * 
 * TODO Atom
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiBlogActionBean extends GWikiBlogBaseActionBean
{
  private String category;

  private boolean rss;

  private String selectedDay = null;

  private Date selectedDayDate = null;

  private String selectedMonth = null;

  private int pageOffset = 0;

  private int pageSize = 10;

  private String blogCategory;

  protected List<GWikiElementInfo> shownBlogEntries = new ArrayList<GWikiElementInfo>();

  @Override
  protected boolean init()
  {
    if (super.init() == false) {
      return false;
    }

    boolean monthSelected = false;
    if (StringUtils.isNotBlank(selectedMonth) == true) {
      try {
        selectedMonthDate = dayHeadReqFormat.get().parse(selectedMonth);
        monthSelected = true;
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

    for (GWikiElementInfo be : blogEntries) {
      if (selectedDayDate != null) {
        if (sameDay(be.getCreatedAt(), selectedDayDate) == false) {
          continue;
        }
      } else {
        if (monthSelected == true && sameMonth(be.getCreatedAt(), selectedMonthDate) == false) {
          continue;
        }
      }
      if (StringUtils.isNotEmpty(blogCategory) == true) {
        List<String> catl = be.getProps().getStringList("BLOG_CATS");
        if (catl.contains(blogCategory) == false) {
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
        .append("<title>" + StringEscapeUtils.escapeXml(blogPage.getTitle()) + "</title>\n") //
        .append("<link>" + wikiContext.getWikiWeb().getWikiConfig().getPublicURL()
            + StringEscapeUtils.escapeXml(blogPage.getId()) + "</link>\n") //

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
          .append("<link>" + wikiContext.getWikiWeb().getWikiConfig().getPublicURL()
              + StringEscapeUtils.escapeXml(ei.getId()) + "</link>\n") //
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
    wikiContext.setRequestAttribute(GWikiRequestAttributeKeys.GWIKI_DISABLE_CHILD_NAV, Boolean.TRUE);
    if (rss == true) {
      deliverRss();
      return noForward();
    }
    return super.onInit();
  }

  protected void renderBlockEntry(GWikiElement el, GWikiExecutableArtefakt<?> exec)
  {
    wikiContext//
        .append("<hr><table width=\"100%\" border=\"0\"><tr><td valign=\"top\">\n") //
        .append("<h2><a href=\"").append(wikiContext.localUrl(el.getElementInfo().getId())).append("\">") //
        .append(esc(el.getElementInfo().getTitle())) //
        .append("</a></h2></td><td align=\"right\"><small>:")//
        .append(wikiContext.getUserDateString(el.getElementInfo().getModifiedAt()))//
        .append(" by ").append(esc(el.getElementInfo().getModifiedBy())) //
        .append("</small></td></tr></table>\n");

    ;

    exec.render(wikiContext);
  }

  public void renderBlogs()
  {
    // wikiContext.append("Blogs here");
    String part = "MainPage";
    Date lastDay = null;
    SimpleDateFormat dayFormat = new SimpleDateFormat(dayHeadFormatPattern, userLocale);
    List<GWikiElementInfo> bel = shownBlogEntries;
    if (pageOffset < 0) {
      pageOffset = 0;
    }
    if (pageOffset > 0) {
      if (bel.size() > pageOffset) {
        bel = bel.subList(pageOffset, bel.size());
      } else {
        bel = Collections.emptyList();
      }
    }
    if (bel.size() > pageSize) {
      bel = bel.subList(0, Math.min(bel.size(), pageSize));
    }

    String thisPl = wikiContext.localUrl(this.blogPageId);
    wikiContext.append("<div class=\"blogPageScroll\">");
    if (pageOffset > 0) {
      int prevPO = Math.min(pageOffset - pageSize, 0);
      wikiContext.append("<a href=\"" + thisPl + "?pageOffset=" + prevPO + "\">")
          .append(esc(translate("gwiki.blog.page.prevPage")))
          .append("</a>&nbsp;");
    }
    if (pageOffset + pageSize < shownBlogEntries.size()) {
      wikiContext.append("<a href=\"" + thisPl + "?pageOffset=" + (pageOffset + pageSize));
      if (StringUtils.isNotEmpty(blogCategory) == true) {
        wikiContext.append("&blogCategory=" + Converter.encodeUrlParam(blogCategory));
      }
      wikiContext.append("\">").append(esc(translate("gwiki.blog.page.nextPage"))).append("</a>&nbsp;");
    }
    wikiContext.append("</div>\n");

    renderBlogCatHeader();

    for (GWikiElementInfo ei : bel) {
      GWikiElement el = wikiContext.getWikiWeb().findElement(ei.getId());
      if (ei.isViewable() == false) {
        continue;
      }
      GWikiArtefakt<?> art = null;
      if (StringUtils.isNotEmpty(part) == true) {
        art = el.getPart(part);
      } else {
        art = el.getMainPart();
      }
      if ((art instanceof GWikiExecutableArtefakt<?>) == false || art instanceof GWikiAttachment) {
        continue;
      }
      Date cday = CalendarControl.toFullDay(el.getElementInfo().getCreatedAt(), userTimeZone);

      if (ObjectUtils.equals(lastDay, cday) == false) {
        wikiContext.append("<h1 class=\"BlogDayHead\">").append(esc(dayFormat.format(cday))).append("</h1>");
      }
      lastDay = cday;
      GWikiExecutableArtefakt<?> exec = (GWikiExecutableArtefakt<?>) art;
      renderBlockEntry(el, exec);
      GWikiContext.setCurrent(wikiContext);
    }

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

  public int getPageOffset()
  {
    return pageOffset;
  }

  public void setPageOffset(int pageOffset)
  {
    this.pageOffset = pageOffset;
  }

  public int getPageSize()
  {
    return pageSize;
  }

  public void setPageSize(int pageCount)
  {
    this.pageSize = pageCount;
  }

  public String getBlogCategory()
  {
    return blogCategory;
  }

  public void setBlogCategory(String blogCategory)
  {
    this.blogCategory = blogCategory;
  }

}

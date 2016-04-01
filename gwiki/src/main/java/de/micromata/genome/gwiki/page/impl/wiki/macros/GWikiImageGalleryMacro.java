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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections15.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiWithHeaderPrepare;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;

/**
 * implement the gallery macro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "Generates a gallery of images",
    params = {
        @MacroInfoParam(name = "pageId", type = MacroParamType.PageId, info = "The parent page for the images.<br>"
            + "If not set uses the current page"),
        @MacroInfoParam(name = "title", info = "Titel"),
        @MacroInfoParam(name = "size", info = "Size of preview", enumValues = { "small", "medium", "large" }),
        @MacroInfoParam(name = "columns", type = MacroParamType.Integer, defaultValue = "4",
            info = "Number of columns the images should be shown"),
        @MacroInfoParam(name = "sort", info = "Fieldname to sort. ", enumValues = { "title", "date" }),
        @MacroInfoParam(name = "reverse", type = MacroParamType.Boolean, info = "Reverse order"),
        @MacroInfoParam(name = "include", info = "Matcher expression, which elements should be included")
    })
public class GWikiImageGalleryMacro extends GWikiMacroBean implements GWikiWithHeaderPrepare
{

  private static final long serialVersionUID = 7197613337870017606L;

  /**
   * if null uses current page.
   */
  private String pageId;

  private String title;

  private int columns = 4;

  /**
   * title, date
   */
  private String sort;

  private boolean reverse;

  /**
   * small, medium, large
   */
  private String size;

  /**
   * Contains a matcher
   */
  private String include;

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (StringUtils.isEmpty(pageId) == true) {
      pageId = ctx.getCurrentElement().getElementInfo().getId();
    }
    if (StringUtils.isEmpty(size) == true) {
      size = "medium";
    }
    List<GWikiElementInfo> childs = ctx.getElementFinder().getPageAttachments(pageId);
    if (StringUtils.isNotEmpty(include) == true) {
      Matcher<String> matcher = new BooleanListRulesFactory<String>().createMatcher(include);
      List<GWikiElementInfo> nch = new ArrayList<GWikiElementInfo>();
      for (GWikiElementInfo ci : childs) {
        if (matcher.match(ci.getId()) == true) {
          nch.add(ci);
        }
      }
      childs = nch;
    }
    Comparator<GWikiElementInfo> comparator = new GWikiElementByChildOrderComparator(new GWikiElementByOrderComparator(
        new GWikiElementByIntPropComparator("ORDER", 0)));
    if (StringUtils.isNotBlank(sort) == true) {

      if (sort.equals("title") == true) {
        comparator = new GWikiElementByPropComparator("TITLE");
      } else if (sort.equals("date") == true) {
        comparator = new GWikiElementByPropComparator("MODIFIEDAT");
      } else {
        comparator = new GWikiElementByPropComparator(sort);
      }
    }
    if (reverse == true) {
      comparator = new ReverseComparator<GWikiElementInfo>(comparator);
    }
    Collections.sort(childs, comparator);

    ctx.append("<table class=\"gwikiimagegallery\" border=\"1\" cellspacing=\"0\">");
    if (StringUtils.isEmpty(title) == false) {
      ctx.append("<tr><th colspan=\"").append(columns).append("\">").append(ctx.escape(title)).append("</th></tr>\n");
    }
    int i = 0;
    int colNo = 0;
    for (GWikiElementInfo c : childs) {
      if (colNo == 0) {
        ctx.append("<tr>");
      }
      ctx.append("<td valign=\"top\">");

      GWikiFragmentImage image = new GWikiFragmentImage(c.getId());
      image.setThumbnail(size);
      image.render(ctx);
      ctx.append("</td>");
      if (colNo + 1 >= columns) {
        ctx.append("</tr>");
        colNo = 0;
      } else {
        ++colNo;
      }
      ++i;
    }
    ctx.append("</tr></table>");
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiWithHeaderPrepare#prepareHeader(de.micromata.genome.gwiki.page.
   * GWikiContext, de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public void prepareHeader(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.getRequiredJs().add("static/js/jquery.thumbs.js");
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public int getColumns()
  {
    return columns;
  }

  public void setColumns(int columns)
  {
    this.columns = columns;
  }

  public String getSort()
  {
    return sort;
  }

  public void setSort(String sort)
  {
    this.sort = sort;
  }

  public boolean isReverse()
  {
    return reverse;
  }

  public void setReverse(boolean reverse)
  {
    this.reverse = reverse;
  }

  public String getSize()
  {
    return size;
  }

  public void setSize(String size)
  {
    this.size = size;
  }

  public String getInclude()
  {
    return include;
  }

  public void setInclude(String include)
  {
    this.include = include;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

}

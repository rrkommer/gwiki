/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.11.2009
// Copyright Micromata 02.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * generates toc with children.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiChildrenMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = -1774971783779837553L;

  private boolean all;

  private int depth = 99;

  private String style;

  private String excerpt;

  private String page;

  private int first;

  private String sort;

  private boolean reverse = false;

  /**
   * all, gwiki, attachment
   */
  private String type = "gwiki";

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return true;
    }
    GWikiElementInfo ei = ctx.getWikiElement().getElementInfo();
    if (StringUtils.isEmpty(page) == false) {
      ei = ctx.getWikiWeb().findElementInfo(page);
    }
    renderChildToc(ei, 1, ctx);
    return true;
  }

  protected void renderChildToc(GWikiElementInfo ei, int level, GWikiContext ctx)
  {
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return;
    }
    if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, ei) == false) {
      return;
    }
    boolean allTypes = StringUtils.equals(type, "all");
    List<GWikiElementInfo> cl = ctx.getElementFinder().getDirectChilds(ei);
    if (allTypes == false) {
      List<GWikiElementInfo> ncl = new ArrayList<GWikiElementInfo>();
      for (GWikiElementInfo ci : cl) {
        if (StringUtils.equals(ci.getProps().getStringValue(GWikiPropKeys.TYPE), type) == false) {
          continue;
        }
        ncl.add(ci);
      }
      cl = ncl;
    }
    if (StringUtils.equalsIgnoreCase(sort, "title") == true) {
      Collections.sort(cl, new GWikiElementByOrderComparator(new GWikiElementByPropComparator("TITLE")));
    } else if (StringUtils.equalsIgnoreCase(sort, "modifiedat") == true) {
      Collections.sort(cl, new GWikiElementByOrderComparator(new GWikiElementByPropComparator("MODIFIEDAT")));
    } else {
      Collections.sort(cl, new GWikiElementByOrderComparator(new GWikiElementByIntPropComparator("ORDER", 0)));
    }

    if (cl.isEmpty() == true)
      return;
    ctx.append("<ul>");
    for (GWikiElementInfo ci : cl) {
      if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, ci) == false) {
        continue;
      }
      ctx.append("<li>").append(ctx.renderLocalUrl(ci.getId())).append("</li>\n");
      if (level + 1 > depth) {
        continue;
      }
      renderChildToc(ci, level + 1, ctx);
    }
    ctx.append("</ul>");
  }

  public boolean isAll()
  {
    return all;
  }

  public void setAll(boolean all)
  {
    this.all = all;
  }

  public int getDepth()
  {
    return depth;
  }

  public void setDepth(int depth)
  {
    this.depth = depth;
  }

  public String getStyle()
  {
    return style;
  }

  public void setStyle(String style)
  {
    this.style = style;
  }

  public String getExcerpt()
  {
    return excerpt;
  }

  public void setExcerpt(String excerpt)
  {
    this.excerpt = excerpt;
  }

  public String getPage()
  {
    return page;
  }

  public void setPage(String page)
  {
    this.page = page;
  }

  public int getFirst()
  {
    return first;
  }

  public void setFirst(int first)
  {
    this.first = first;
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

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

}

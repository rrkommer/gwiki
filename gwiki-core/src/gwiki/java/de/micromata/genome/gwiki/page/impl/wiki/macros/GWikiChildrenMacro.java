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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiCollectMacroFragmentVisitor;

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

  /**
   * Only for gwiki-childs. Include also pageintro's
   */
  private boolean withPageIntro = false;

  /**
   * Only for gwiki childs. Include also toc's inside the pages.
   */
  private boolean withPageTocs = false;

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

  protected GWikiWikiPageArtefakt getWikiFromElement(GWikiElementInfo ci, GWikiContext ctx)
  {
    GWikiElement el = ctx.getWikiWeb().findElement(ci.getId());
    if (el == null) {
      return null;
    }
    GWikiArtefakt< ? > ma = el.getMainPart();
    if (ma instanceof GWikiWikiPageArtefakt) {
      return (GWikiWikiPageArtefakt) ma;
    }
    Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
    el.collectParts(map);
    ma = map.get("MainPage");
    if (ma instanceof GWikiWikiPageArtefakt) {
      return (GWikiWikiPageArtefakt) ma;
    }
    for (GWikiArtefakt< ? > a : map.values()) {
      if (a instanceof GWikiWikiPageArtefakt) {
        return (GWikiWikiPageArtefakt) a;
      }
    }
    return null;
  }

  protected void renderChild(GWikiElementInfo ci, GWikiContext ctx)
  {

    ctx.append("<li>").append(ctx.renderLocalUrl(ci.getId()));
    if (withPageIntro == true || withPageTocs == true) {

      GWikiWikiPageArtefakt wiki = getWikiFromElement(ci, ctx);
      if (wiki != null) {
        if (wiki.compileFragements(ctx) == true) {
          GWikiContent cont = wiki.getCompiledObject();
          if (withPageIntro == true) {
            GWikiCollectMacroFragmentVisitor col = new GWikiCollectMacroFragmentVisitor("pageintro");
            cont.iterate(col);
            if (col.getFound().isEmpty() == false) {
              GWikiMacroFragment mf = (GWikiMacroFragment) col.getFound().get(0);
              ctx.append("<small>");
              mf.renderChilds(ctx);
              ctx.append("</small><br/>");
            }
          } else if (withPageTocs == true) {
            // TODO gwiki
          }
        }
      }
    }
    ctx.append("</li>\n");

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
      renderChild(ci, ctx);

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

  public boolean isWithPageIntro()
  {
    return withPageIntro;
  }

  public void setWithPageIntro(boolean withPageIntro)
  {
    this.withPageIntro = withPageIntro;
  }

  public boolean isWithPageTocs()
  {
    return withPageTocs;
  }

  public void setWithPageTocs(boolean withPageTocs)
  {
    this.withPageTocs = withPageTocs;
  }

}

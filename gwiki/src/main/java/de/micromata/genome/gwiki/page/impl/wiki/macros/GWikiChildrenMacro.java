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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiContextUtils;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiCollectFragmentTypeVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiCollectMacroFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;

/**
 * generates toc with children.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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

  private boolean viewAll = false;

  private boolean withEditLinks = false;

  public GWikiChildrenMacro()
  {
    setRenderModes(GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NoWrapWithP));
  }

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
    // ctx.append("</li>\n");
    return true;
  }

  protected void renderChild(GWikiElementInfo ci, GWikiContext ctx)
  {

    ctx.append("\n<li>").append(ctx.renderLocalUrl(ci.getId()));
    if (withEditLinks == true) {
      if (ctx.getWikiWeb().getAuthorization().isAllowToEdit(ctx, ci) == true) {
        ctx.append("&nbsp;<a href=\"")//
            .append(ctx.localUrl("edit/EditPage?pageId=")).append(ci.getId())//
            .append("&amp;backUrl=").append(ctx.getWikiElement().getElementInfo().getId())
            .append("\">").append(ctx.getTranslated("gwiki.macro.children.edit")).append("</a>");
      }
      ctx.append("&nbsp;<a href=\"")//
          .append(ctx.localUrl("edit/PageInfo?pageId=")).append(ci.getId())//
          .append("&amp;backUrl=").append(ctx.getWikiElement().getElementInfo().getId())
          .append("\">").append(ctx.getTranslated("gwiki.macro.children.info")).append("</a>&nbsp;");
    }
    if (withPageIntro == true || withPageTocs == true) {

      GWikiWikiPageArtefakt wiki = GWikiContextUtils.getWikiFromElement(ci, ctx);
      if (wiki != null) {
        if (wiki.compileFragements(ctx) == true) {
          GWikiContent cont = wiki.getCompiledObject();
          if (withPageIntro == true) {
            GWikiCollectMacroFragmentVisitor col = new GWikiCollectMacroFragmentVisitor("pageintro");
            cont.iterate(col);
            if (col.getFound().isEmpty() == false) {
              GWikiMacroFragment mf = (GWikiMacroFragment) col.getFound().get(0);
              ctx.append("<small><br/>");

              if (mf.getChilds().size() == 1 && mf.getChilds().get(0) instanceof GWikiFragmentP) {
                GWikiFragmentP p = (GWikiFragmentP) mf.getChilds().get(0);
                p.renderChilds(ctx);
              } else {
                mf.renderChilds(ctx);
              }
              ctx.append("<br/></small><br/>");
            }
          }
          if (withPageTocs == true) {
            GWikiCollectFragmentTypeVisitor col = new GWikiCollectFragmentTypeVisitor(GWikiFragmentHeading.class);
            cont.iterate(col);
            int lastLevel = 1;
            for (GWikiFragment frag : col.getFound()) {
              GWikiFragmentHeading hf = (GWikiFragmentHeading) frag;
              int cl = hf.getLevel();
              if (cl > lastLevel) {
                for (int l = lastLevel; l < cl; ++l) {
                  ctx.append("<ul>");
                }
              }
              if (cl < lastLevel) {
                for (int l = lastLevel; l > cl; --l) {
                  ctx.append("</ul>");
                }
              }
              ctx.append("\n<li><a href=\"").append(ctx.localUrl(ci.getId())).append("#");
              ctx.append(hf.getLinkText(ctx)).append("\">").append(hf.getLinkText(ctx)).append("</a></li>");
              lastLevel = cl;
            }
            for (int l = lastLevel; l > 1; --l) {
              ctx.append("</ul>");
            }
          }
        }
      }
    }

  }

  protected void renderChildToc(GWikiElementInfo ei, int level, GWikiContext ctx)
  {
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return;
    }
    if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, ei) == false) {
      return;
    }
    if (ei.isNoToc() == true) {
      return;
    }
    boolean allTypes = StringUtils.equals(type, "all");
    List<GWikiElementInfo> cl;
    if (viewAll == false) {
      cl = ctx.getElementFinder().getDirectChilds(ei);
    } else {
      cl = ctx.getElementFinder().getAllDirectChilds(ei);
    }
    if (allTypes == false) {
      List<GWikiElementInfo> ncl = new ArrayList<GWikiElementInfo>();
      for (GWikiElementInfo ci : cl) {
        if (StringUtils.equals(ci.getType(), type) == false) {
          continue;
        }
        if (viewAll == true || ci.isNoToc() == true) {
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
      Collections.sort(cl, new GWikiElementByChildOrderComparator(new GWikiElementByOrderComparator(new GWikiElementByIntPropComparator(
          "ORDER", 0))));
    }

    if (cl.isEmpty() == true) {
      return;
    }
    String xmlidattr = "";
    if (level == 1) {
      xmlidattr = " id='" + ctx.genHtmlId("childrentoc") + "'";
    }
    ctx.append("\n<ul" + xmlidattr + ">\n");
    for (GWikiElementInfo ci : cl) {
      if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, ci) == false) {
        if (viewAll == true && withEditLinks == true) {
          if (ctx.getWikiWeb().getAuthorization().isAllowToEdit(ctx, ci) == false) {
            continue;
          }
        } else {
          continue;
        }
      }
      renderChild(ci, ctx);

      if (level + 1 > depth) {
        ctx.append("</li>\n"); // close child
        continue;
      }
      renderChildToc(ci, level + 1, ctx);
      ctx.append("</li>\n"); // close child
    }
    ctx.append("\n</ul>\n");
  }

  public static final ThreadLocal<SimpleDateFormat> SITE_CONTENT_FORMAT = new ThreadLocal<SimpleDateFormat>() {

    @Override
    protected SimpleDateFormat initialValue()
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      sdf.setTimeZone(GWikiProps.UTC_TIMEZONE);
      return sdf;
    }

  };

  public static void renderSitemapChildren(GWikiContext wikiContext, GWikiElementInfo ei, String defaultChageFreq)
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToView(wikiContext, ei) == false) {
      return;
    }
    if (ei.isNoToc() == true) {
      return;
    }
    wikiContext.getWikiWeb().getWikiConfig().getPublicURL();
    wikiContext.append("<url><loc>").append(wikiContext.globalUrl(ei.getId())).append("</loc>\n"); //
    Date d = ei.getModifiedAt();
    if (d != null) {
      wikiContext.append("<lastmod>").append(SITE_CONTENT_FORMAT.get().format(d)).append("</lastmod>");
    }
    if (StringUtils.isNotEmpty(defaultChageFreq) == true) {
      wikiContext.append("<changefreq>").append(StringEscapeUtils.escapeXml(defaultChageFreq)).append("</changefreq>\n");
    }

    wikiContext.append("</url>\n");

    List<GWikiElementInfo> cl = wikiContext.getElementFinder().getAllDirectChilds(ei);
    for (GWikiElementInfo ce : cl) {
      renderSitemapChildren(wikiContext, ce, defaultChageFreq);
    }
  }

  public static void renderSitemap(GWikiContext wikiContext, String rootId, String defaultChageFreq)
  {
    if (StringUtils.isEmpty(rootId) == true) {
      rootId = wikiContext.getWikiWeb().getWikiConfig().getWelcomePageId();
    }
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(rootId);
    if (ei != null) {
      renderSitemapChildren(wikiContext, ei, defaultChageFreq);
    }

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

  public boolean isViewAll()
  {
    return viewAll;
  }

  public void setViewAll(boolean viewAll)
  {
    this.viewAll = viewAll;
  }

  public boolean isWithEditLinks()
  {
    return withEditLinks;
  }

  public void setWithEditLinks(boolean withEditLinks)
  {
    this.withEditLinks = withEditLinks;
  }

}

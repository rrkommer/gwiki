/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.10.2009
// Copyright Micromata 22.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.util.types.Converter;

public class GWikiFragementLink extends GWikiFragmentChildsBase
{

  private static final long serialVersionUID = 7539226429764305415L;

  private String originTarget;

  private String target;

  private String title;

  private String tip;

  private boolean titleDefined = false;

  public GWikiFragementLink(String target)
  {
    this.originTarget = target;
    List<String> elems = Converter.parseStringTokens(target, "|", false);
    if (elems.size() == 0) {
      target = "";
    } else if (elems.size() == 1) {
      if (isGlobalUrl(target) == false) {
        this.target = GWikiContext.getPageIdFromTitle(target);
      } else {
        this.target = target;
      }
      this.title = target;
    } else if (elems.size() == 2) {
      this.target = elems.get(1);
      this.title = elems.get(0);
      titleDefined = true;
    } else {
      this.target = elems.get(1);
      this.title = elems.get(0);
      this.tip = elems.get(2);
      titleDefined = true;
    }

  }

  public boolean isTitleDefined()
  {
    return titleDefined;
  }

  public void getSource(StringBuilder sb)
  {
    sb.append("[");
    if (titleDefined == true) {
      sb.append(title).append("|");
    }
    sb.append(target);
    sb.append("]");
  }

  public static boolean isGlobalUrl(String url)
  {
    return url.contains(":") == true;
  }

  public void renderTitle(GWikiContext ctx, String ttitel)
  {
    if (titleDefined == true || getChilds().size() == 0) {
      ctx.append(ttitel);
    } else {
      this.renderChilds(ctx);
    }

  }

  public boolean render(GWikiContext ctx)
  {

    if (RenderModes.NoLinks.isSet(ctx.getRenderMode()) == true) {
      if (StringUtils.isNotBlank(title) == true) {
        ctx.append(StringEscapeUtils.escapeHtml(title));
      } else if (isGlobalUrl(target) == true) {
        ctx.append(StringEscapeUtils.escapeHtml(target));
      } else {
        String url = target;
        if (url.indexOf('#') != -1) {
          url = url.substring(0, url.indexOf('#'));
          // localAnchor = url.substring(url.indexOf('#'));
        }
        GWikiElementInfo ei = ctx.getWikiWeb().findElementInfo(url);
        if (ei != null) {
          ctx.append(StringEscapeUtils.escapeHtml(ctx.getTranslatedProp(ei.getTitle())));
        } else {
          ctx.append(StringEscapeUtils.escapeHtml(url));
        }
      }
      return true;
    }
    String url = target;
    String ttitel = title;
    boolean targetExists = false;
    boolean allowToView = true;
    boolean allowToCreate = false;
    String parentUrl = null;
    String localAnchor = null;
    if (ctx.getWikiElement() != null) {
      parentUrl = ctx.getWikiElement().getElementInfo().getId();
    }
    if (isGlobalUrl(url) == false) {
      if (url.indexOf('#') != -1) {
        url = url.substring(0, url.indexOf('#'));
        localAnchor = url.substring(url.indexOf('#'));
      }
      GWikiElementInfo ei = ctx.getWikiWeb().findElementInfo(url);
      if (url.indexOf('/') == -1 && ei == null && ctx.getWikiElement() != null) {
        String pp = GWikiContext.getParentDirPathFromPageId(ctx.getWikiElement().getElementInfo().getId());
        String np = pp + url;
        ei = ctx.getWikiWeb().findElementInfo(np);
        if (ei != null) {
          url = ei.getId();
        }
      }
      if (ei != null && isTitleDefined() == false) {
        ttitel = ctx.getTranslatedProp(ei.getTitle());
      }
      if (ei != null) {
        targetExists = true;
        allowToView = ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, ei);
        url = ctx.localUrl(url);
      } else {
        allowToCreate = ctx.getWikiWeb().getAuthorization().isAllowToCreate(ctx, ctx.getWikiElement().getElementInfo());
      }
    } else {
      targetExists = true;
    }
    if (targetExists == false) {
      if (allowToCreate == true) {
        ctx.append("<a href='")//
            .append(ctx.localUrl("edit/EditPage?newPage=true&parentPageId="))//
            .append(WebUtils.encodeUrlParam(parentUrl))//
            .append("&pageId=") //
            .append(WebUtils.encodeUrlParam(url))//
            .append("&title=")//
            .append(WebUtils.encodeUrlParam(ttitel))//
            .append("'");
        if (StringUtils.isNotEmpty(tip) == true) {
          ctx.append(" title='", StringEscapeUtils.escapeHtml(tip), "'");
        }
        ctx.append(">");
        renderTitle(ctx, ttitel);
        ctx.append("</a>");
      } else {
        renderTitle(ctx, ttitel);
      }
    } else { // exists
      if (allowToView == false) {
        renderTitle(ctx, ttitel);
      } else {
        String turl = url;
        if (localAnchor != null) {
          turl = turl + "#" + localAnchor;
        }
        ctx.append("<a href=\"", turl, "\"");
        if (StringUtils.isNotEmpty(tip) == true) {
          ctx.append(" title='", StringEscapeUtils.escapeHtml(tip), "'");
        } else {
          ctx.append(" title='", StringEscapeUtils.escapeHtml(ttitel), "'");
        }
        ctx.append(">");
        renderTitle(ctx, ttitel);
        ctx.append("</a>");
      }

    }

    return true;
  }

  public String getTarget()
  {
    return target;
  }

  public void setTarget(String target)
  {
    this.target = target;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getTip()
  {
    return tip;
  }

  public void setTip(String tip)
  {
    this.tip = tip;
  }
}

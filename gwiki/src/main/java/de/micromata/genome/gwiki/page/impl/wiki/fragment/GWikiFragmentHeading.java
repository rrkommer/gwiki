/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   01.11.2009
// Copyright Micromata 01.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;

public class GWikiFragmentHeading extends GWikiFragmentChildsBase
{

  private static final long serialVersionUID = 7292812809082522573L;

  public static final String GWIKI_LAST_HEADING_LEVEL = "GWIKI_LAST_HEADING_LEVEL";

  private int level;

  private String text;

  private String linkText = null;

  public GWikiFragmentHeading(int level)
  {
    this.level = level;
  }

  public GWikiFragmentHeading(int level, String text)
  {
    this.level = level;
    this.text = text;
  }

  public void renderBody(GWikiContext ctx)
  {
    if (StringUtils.isNotBlank(text) == true) {
      ctx.append(StringEscapeUtils.escapeHtml(text));
    } else {
      renderChilds(ctx);
    }
  }

  public String getLinkText(GWikiContext ctx)
  {
    if (StringUtils.isNotEmpty(text) == true) {
      return text;
    }
    if (StringUtils.isNotBlank(linkText) == true) {
      return linkText;
    }
    GWikiStandaloneContext stc = new GWikiStandaloneContext(ctx);
    stc.setRenderMode(RenderModes.combine(RenderModes.ForText, RenderModes.NoImages, RenderModes.NoLinks));
    stc.setWikiElement(ctx.getWikiElement());
    renderChilds(stc);
    // stc.flush();
    linkText = stc.getOutString();
    return linkText;
  }

  public boolean render(GWikiContext ctx)
  {
    ctx.append("<h", Integer.toString(level), ">");
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == false) {
      ctx.append("<a name=\"", getLinkText(ctx), "\" target=\"_top\"></a>");
    }
    renderBody(ctx);
    ctx.append("</h", Integer.toString(level), ">");
    return true;
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    if (sb.length() > 0) {
      char lc = sb.charAt(sb.length() - 1);
      if (lc != '\n' && lc != '|') {
        sb.append("\n");
      }
    }
    sb.append("h").append(level).append(".");
    getChildSouce(sb);
    sb.append("\n");
  }

  public int getLevel()
  {
    return level;
  }

  public void setLevel(int level)
  {
    this.level = level;
  }

  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

}

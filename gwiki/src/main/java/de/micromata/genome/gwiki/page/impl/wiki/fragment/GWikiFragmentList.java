/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.12.2009
// Copyright Micromata 22.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiFragmentList extends GWikiFragmentChildsBase
{

  private static final long serialVersionUID = -10964121817921598L;

  private String listTag;

  public GWikiFragmentList(String listTag)
  {
    this.listTag = listTag;
  }

  public GWikiFragmentList(String listTag, List<GWikiFragment> childs)
  {
    super(childs);
    this.listTag = listTag;
  }

  public boolean sameType(GWikiFragmentList other)
  {
    return StringUtils.equals(this.listTag, other.listTag);
  }

  public boolean render(GWikiContext ctx)
  {
    final char type = listTag.charAt(listTag.length() - 1);
    final String tag = tagForList(type);
    final String attrs = attrForList(type);
    // if childs is ListFragment no li
    ctx.append("<").append(tag).append(attrs).append(">");
    renderChilds(ctx);
    ctx.append("</").append(tag).append(">");
    return true;
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
      sb.append("\n");
    }
    getChildSouce(sb);
  }

  protected String tagForList(char tk)
  {
    switch (tk) {
      case '-':
        return "ul";
      case '*':
        return "ul";
      case '#':
        return "ol";
      default:
        return "ul";
    }
  }

  protected String attrForList(char tk)
  {
    switch (tk) {
      case '-':
        return " class=\"minus\" type=\"square\"";
      case '*':
        return " class=\"star\"";
      default:
        return "";
    }
  }

  public String getListTag()
  {
    return listTag;
  }

  public void setListTag(String listTag)
  {
    this.listTag = listTag;
  }

}

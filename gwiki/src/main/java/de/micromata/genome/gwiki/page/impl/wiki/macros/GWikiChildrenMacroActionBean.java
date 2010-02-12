/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.11.2009
// Copyright Micromata 09.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * An ActionBean for the childrean macro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiChildrenMacroActionBean extends ActionBeanBase
{
  private boolean all;

  private int depth = 99;

  private String style;

  private String excerpt;

  private String page;

  private int first;

  private String sort;

  private boolean reverse = false;

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
}

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.10.2009
// Copyright Micromata 18.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import org.apache.commons.lang.StringEscapeUtils;

public class GWikiFragmentText extends GWikiFragmentHtml
{

  private static final long serialVersionUID = 7127807753044155663L;

  private StringBuilder text = new StringBuilder();

  public GWikiFragmentText(String text)
  {
    super((String) null);
    this.text.append(text);
  }

  public GWikiFragmentText(GWikiFragmentText other)
  {
    super(other);
  }

  public void addText(String text)
  {
    this.text.append(text);
    this.html = null;
  }

  @Override
  public Object clone()
  {
    return new GWikiFragmentText(this);
  }

  @Override
  public String getHtml()
  {
    if (html == null) {
      html = StringEscapeUtils.escapeHtml(text.toString());
    }
    return html;
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append(text.toString());
  }

}

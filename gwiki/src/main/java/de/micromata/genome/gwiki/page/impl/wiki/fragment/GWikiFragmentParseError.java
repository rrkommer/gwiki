/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   23.12.2009
// Copyright Micromata 23.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

public class GWikiFragmentParseError extends GWikiFragmentDecorator
{

  private static final long serialVersionUID = -3118476516172674397L;

  private String text;

  public GWikiFragmentParseError(GWikiFragmentParseError other)
  {
    super(other);
  }

  public GWikiFragmentParseError(String text)
  {
    super("<color=\"red\">", "</color>");
    this.text = text;
    addChild(new GWikiFragmentText(text));
  }

  public void getSource(StringBuilder sb)
  {
    sb.append(text);
  }

}

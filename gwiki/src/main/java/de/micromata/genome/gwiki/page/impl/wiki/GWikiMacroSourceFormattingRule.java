/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.01.2010
// Copyright Micromata 03.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

public class GWikiMacroSourceFormattingRule
{
  private String beforeStart = "";

  private String afterStart = "";

  private String beforeEnd = "";

  private String afterEnd = "";

  private boolean trimContontent;

  public GWikiMacroSourceFormattingRule(boolean trimContontent, String beforeStart, String afterStart)
  {
    this.trimContontent = trimContontent;
    this.beforeStart = beforeStart;
    this.afterStart = afterStart;
  }

  public GWikiMacroSourceFormattingRule(boolean trimContontent, String beforeStart, String afterStart, String beforeEnd, String afterEnd)
  {
    this.trimContontent = trimContontent;
    this.beforeStart = beforeStart;
    this.afterStart = afterStart;
    this.beforeEnd = beforeEnd;
    this.afterEnd = afterEnd;
  }

  public String getBeforeStart()
  {
    return beforeStart;
  }

  public void setBeforeStart(String beforeStart)
  {
    this.beforeStart = beforeStart;
  }

  public String getAfterStart()
  {
    return afterStart;
  }

  public void setAfterStart(String afterStart)
  {
    this.afterStart = afterStart;
  }

  public String getBeforeEnd()
  {
    return beforeEnd;
  }

  public void setBeforeEnd(String beforeEnd)
  {
    this.beforeEnd = beforeEnd;
  }

  public String getAfterEnd()
  {
    return afterEnd;
  }

  public void setAfterEnd(String afterEnd)
  {
    this.afterEnd = afterEnd;
  }

  public boolean isTrimContontent()
  {
    return trimContontent;
  }

  public void setTrimContontent(boolean trimContontent)
  {
    this.trimContontent = trimContontent;
  }
}

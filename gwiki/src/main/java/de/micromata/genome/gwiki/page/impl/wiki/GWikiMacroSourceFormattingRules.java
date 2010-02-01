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

import java.util.HashMap;
import java.util.Map;

public class GWikiMacroSourceFormattingRules
{
  static Map<String, GWikiMacroSourceFormattingRule> rules = new HashMap<String, GWikiMacroSourceFormattingRule>();

  private static GWikiMacroSourceFormattingRule mk(boolean trim, String beforeStart, String afterStart)
  {
    return new GWikiMacroSourceFormattingRule(trim, beforeStart, afterStart);
  }

  private static GWikiMacroSourceFormattingRule mk(boolean trim, String beforeStart, String arterStart, String beforeEnd, String afterEnd)
  {
    return new GWikiMacroSourceFormattingRule(trim, beforeStart, beforeEnd, beforeEnd, afterEnd);
  }

  static {
    rules.put("table", mk(true, "", "\n", "\n", ""));
    rules.put("tr", mk(true, "", "\n", "\n", ""));
  }
}

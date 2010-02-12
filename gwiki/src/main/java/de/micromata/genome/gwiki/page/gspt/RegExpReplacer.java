/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.01.2007
// Copyright Micromata 14.01.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.util.regex.Pattern;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author roger
 * 
 */
public abstract class RegExpReplacer extends ReplacerBase
{
  private Pattern startPattern;

  private Pattern endPattern;

  public RegExpReplacer(String startPatternStr, String endPatternStr)
  {
    startPattern = Pattern.compile(startPatternStr, Pattern.MULTILINE + Pattern.DOTALL);
    endPattern = Pattern.compile(endPatternStr, Pattern.MULTILINE + Pattern.DOTALL);
  }

  public Pattern getStartPattern()
  {
    return startPattern;
  }

  public Pattern getEndPattern()
  {
    return endPattern;
  }

  public String getEnd()
  {
    return endPattern.pattern();
  }

  public String getStart()
  {
    return startPattern.pattern();
  }

}

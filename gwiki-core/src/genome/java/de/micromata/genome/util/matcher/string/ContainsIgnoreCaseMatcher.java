/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   28.11.2009
// Copyright Micromata 28.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher.string;

import org.apache.commons.lang.StringUtils;

public class ContainsIgnoreCaseMatcher<T> extends StringPatternMatcherBase<T>
{
  public ContainsIgnoreCaseMatcher()
  {

  }

  public ContainsIgnoreCaseMatcher(String pattern)
  {
    super(pattern);
  }

  public boolean matchString(String token)
  {
    return StringUtils.containsIgnoreCase(token, pattern);
  }

  public String toString()
  {
    return "<EXPR>.containsIgnoreCase(" + pattern + ")";
  }
}

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.11.2009
// Copyright Micromata 14.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher;

/**
 * matcher.match(object) returns true if matcher.object >= arg
 * 
 * @author roger@micromata.de
 * 
 */
public class MoreThanOrEqualMatcher<T extends Comparable<T>> extends ComparatorMatcherBase<T>
{

  private static final long serialVersionUID = 470081892409452114L;

  public MoreThanOrEqualMatcher()
  {
  }

  public MoreThanOrEqualMatcher(T other)
  {
    super(other);
  }

  public boolean match(T object)
  {
    if (other == null || other == null)
      return false;
    return other.compareTo(object) <= 0;
  }
  public String toString()
  {
    return "(" + other.toString() + " >= EXPR)";
  }
}

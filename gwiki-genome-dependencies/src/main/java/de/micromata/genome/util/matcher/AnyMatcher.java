/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    jensi@micromata.de
// Created   30.12.2008
// Copyright Micromata 30.12.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher;

/**
 * matches if at least one given matcher matches.
 */
public class AnyMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = 4055207023527099498L;

  private Iterable<Matcher< ? super T>> matchers;

  public AnyMatcher(Iterable<Matcher< ? super T>> matchers)
  {
    this.matchers = matchers;
  }

  public boolean match(T object)
  {
    for (Matcher< ? super T> m : matchers) {
      if (m.match(object) == true) {
        return true;
      }
    }
    return false;
  }

  public String toString()
  {
    return "*";
  }

}

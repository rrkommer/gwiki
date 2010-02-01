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

import java.util.HashMap;
import java.util.Map;

/**
 * always growing, never shrinking, never invalidating => must eventually be recreated.
 * 
 * @author jens@micromata.de
 */
public class GrowingCacheMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = 2201308849362191628L;

  private Map<T, MatchResult> cache = new HashMap<T, MatchResult>();

  private Matcher<T> backend;

  private Boolean noMatchValue;

  public GrowingCacheMatcher(Matcher<T> backend, Boolean noMatchValue)
  {
    this.backend = backend;
    this.noMatchValue = noMatchValue;
  }

  public MatchResult apply(T object)
  {
    MatchResult res = cache.get(object);
    if (res == null) {
      res = backend.apply(object);
      cache.put(object, res);
    }
    return res;
  }

  public boolean match(T object)
  {
    MatchResult res = apply(object);
    if (res == MatchResult.MatchPositive) {
      return true;
    } else if (res == MatchResult.MatchNegative) {
      return false;
    }
    if (noMatchValue == null) {
      throw new IllegalStateException(MatchResult.MatchNegative.toString());
    }
    return noMatchValue;
  }

  public static <T> Matcher<T> wrap(Matcher<T> backend, Boolean noMatchValue)
  {
    return new GrowingCacheMatcher<T>(backend, noMatchValue);
  }
}

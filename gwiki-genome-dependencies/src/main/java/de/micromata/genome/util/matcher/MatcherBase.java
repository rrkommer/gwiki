package de.micromata.genome.util.matcher;

import java.util.Collection;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public abstract class MatcherBase<T> implements Matcher<T>
{
  public MatchResult apply(T token)
  {
    return match(token) ? MatchResult.MatchPositive : MatchResult.NoMatch;
  }

  public boolean matchAll(Collection<T> sl, boolean defaultValue)
  {
    boolean matches = defaultValue;
    for (T token : sl) {
      MatchResult mr = apply(token);
      if (mr == MatchResult.NoMatch)
        return false;
    }
    return matches;
  }

  public boolean matchAny(Collection<T> sl, boolean defaultValue)
  {
    boolean matches = defaultValue;
    for (T token : sl) {
      MatchResult mr = apply(token);
      if (mr == MatchResult.MatchPositive)
        return true;
    }
    return matches;
  }
}

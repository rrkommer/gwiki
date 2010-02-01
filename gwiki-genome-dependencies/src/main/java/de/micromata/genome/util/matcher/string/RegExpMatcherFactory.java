package de.micromata.genome.util.matcher.string;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherFactory;

/**
 * Create a RegExpMatcher
 * 
 * @author roger
 * 
 */
public class RegExpMatcherFactory<T> implements MatcherFactory<T>
{

  public Matcher<T> createMatcher(String pattern)
  {
    return new RegExpMatcher<T>(pattern);
  }

  public String getRuleString(Matcher<T> matcher)
  {
    if (matcher instanceof RegExpMatcher) {
      ((RegExpMatcher<T>) matcher).getPattern().toString();
    }
    return "<unknown>";
  }

}

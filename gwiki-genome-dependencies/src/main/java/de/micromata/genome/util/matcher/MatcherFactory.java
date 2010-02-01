package de.micromata.genome.util.matcher;

/**
 * A Factory, which creates a Matcher
 * 
 * @author roger
 * 
 * @param <T>
 */
public interface MatcherFactory<T>
{
  // TODO document me. Was ist wenn pattern null ist, empty ist, etc
  public Matcher<T> createMatcher(String pattern);

  public String getRuleString(Matcher<T> matcher);
}

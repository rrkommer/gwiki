package de.micromata.genome.util.matcher.string;

import org.apache.commons.io.FilenameUtils;

/**
 * Matches if Wildcard pattern matches
 * 
 * @author roger
 * 
 */
public class WildcardMatcher<T> extends StringPatternMatcherBase<T>
{

  private static final long serialVersionUID = 6447659182015217706L;

  public WildcardMatcher()
  {

  }

  public WildcardMatcher(String pattern)
  {
    super(pattern);
  }

  public boolean matchString(String token)
  {
    return FilenameUtils.wildcardMatch(token, pattern) == true;
  }

  public String toString()
  {
    return "<EXPR>.wildCartMatch(" + pattern + ")";
  }
}

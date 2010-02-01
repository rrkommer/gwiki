package de.micromata.genome.util.matcher.string;

/**
 * Matches if string starts with pattern
 * 
 * @author roger
 * 
 */
public class StartWithMatcher<T> extends StringPatternMatcherBase<T>
{
  /**
   * 
   */
  private static final long serialVersionUID = -7078608821658998712L;

  public StartWithMatcher()
  {

  }

  public StartWithMatcher(String pattern)
  {
    super(pattern);
  }

  public boolean matchString(String token)
  {
    return token.startsWith(pattern);
  }

  public String toString()
  {
    return "<EXPR>.startsWith(" + pattern + ")";
  }
}

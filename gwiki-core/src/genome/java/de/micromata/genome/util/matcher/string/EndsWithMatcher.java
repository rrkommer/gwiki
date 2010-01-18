package de.micromata.genome.util.matcher.string;

/**
 * Matches if string ends with pattern
 * 
 * @author roger
 * 
 */
public class EndsWithMatcher<T> extends StringPatternMatcherBase<T>
{

  private static final long serialVersionUID = 3016817124097150838L;

  public EndsWithMatcher()
  {

  }

  public EndsWithMatcher(String pattern)
  {
    super(pattern);
  }

  public boolean matchString(String token)
  {
    return token.endsWith(pattern);
  }

  public String toString()
  {
    return "<EXPR>.endsWith(" + pattern + ")";
  }
}

package de.micromata.genome.util.matcher.string;

/**
 * Matches if string is containing
 * 
 * @author roger
 * 
 */
public class ContainsMatcher<T> extends StringPatternMatcherBase<T>
{

  private static final long serialVersionUID = 6751376242568909350L;

  public ContainsMatcher()
  {

  }

  public ContainsMatcher(String pattern)
  {
    super(pattern);
  }

  public boolean matchString(String token)
  {
    return token.indexOf(pattern) != -1;
  }

  public String toString()
  {
    return "<EXPR>.contains(" + pattern + ")";
  }
}

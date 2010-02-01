package de.micromata.genome.util.matcher.string;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public abstract class StringPatternMatcherBase<T> extends StringMatcherBase<T>
{
  /**
   * 
   */
  private static final long serialVersionUID = -1169791355239547248L;

  protected String pattern;

  public StringPatternMatcherBase()
  {

  }

  public StringPatternMatcherBase(String pattern)
  {
    this.pattern = pattern;
  }

  public String getPattern()
  {
    return pattern;
  }

  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }

  public String toString()
  {
    return "<EXPR>.patternMatch(" + pattern + ")";
  }

}

package de.micromata.genome.util.matcher;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public abstract class PatternMatcherBase<T> extends MatcherBase<T>
{
  /**
   * 
   */
  private static final long serialVersionUID = -2009019329583789214L;
  protected String pattern;

  public PatternMatcherBase()
  {
  }

  public PatternMatcherBase(String pattern)
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
}

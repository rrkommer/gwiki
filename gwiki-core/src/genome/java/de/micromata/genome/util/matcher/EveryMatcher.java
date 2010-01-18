package de.micromata.genome.util.matcher;

/**
 * Matches always.
 * 
 * @author roger
 * 
 * @param <T>
 */
public class EveryMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = -6603198094053117381L;

  public boolean match(T token)
  {
    return true;
  }

  public String toString()
  {
    return "*";
  }

}

package de.micromata.genome.util.matcher;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public class NoneMatcher<T> extends MatcherBase<T>
{


  private static final long serialVersionUID = -5981777834465779179L;

  public boolean match(T token)
  {
    return false;
  }

  public String toString()
  {
    return "<NEVER>";
  }
}

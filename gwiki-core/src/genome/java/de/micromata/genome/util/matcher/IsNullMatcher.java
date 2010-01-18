package de.micromata.genome.util.matcher;

/**
 * Matches if token is null
 * 
 * @author roger
 * 
 * @param <T>
 */
public class IsNullMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = -6603198094053117381L;

  public boolean match(T token)
  {
    return token == null;
  }

  public String toString()
  {
    return "<expr>.isNull()";
  }

}

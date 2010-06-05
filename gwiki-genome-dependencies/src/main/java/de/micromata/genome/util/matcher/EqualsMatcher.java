package de.micromata.genome.util.matcher;

/**
 * Matches if pattern is equals to token
 * 
 * @author roger
 * 
 */
public class EqualsMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = -531763040829078392L;

  private T other;

  public EqualsMatcher()
  {

  }

  public EqualsMatcher(T other)
  {
    this.other = other;
  }

  public boolean match(T token)
  {
    if (token == null && other == null) {
      return true;
    }
    if (token == null && other != null)
      return false;
    if (token != null && other == null)
      return false;
    return token.equals(other);
  }

  public T getOther()
  {
    return other;
  }

  public void setOther(T other)
  {
    this.other = other;
  }

  public String toString()
  {
    return other.toString() + " = <Expr>";
  }
}

package de.micromata.genome.util.matcher;

/**
 * Matches if nested matcher does not matches
 * 
 * @author roger
 * 
 */
public class NotMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = 4329354733404236006L;

  private Matcher<T> nested;

  public NotMatcher()
  {

  }

  public NotMatcher(Matcher<T> nested)
  {
    this.nested = nested;
  }

  public boolean match(T token)
  {
    return nested.match(token) == false;
  }

  public Matcher<T> getNested()
  {
    return nested;
  }

  public void setNested(Matcher<T> nested)
  {
    this.nested = nested;
  }

  public String toString()
  {
    return "!" + nested.toString();
  }

}

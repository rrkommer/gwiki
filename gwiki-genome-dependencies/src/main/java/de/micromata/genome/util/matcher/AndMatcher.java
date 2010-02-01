package de.micromata.genome.util.matcher;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public class AndMatcher<T> extends LeftRightMatcherBase<T>
{

  private static final long serialVersionUID = -2200236053889779000L;

  public AndMatcher(Matcher<T> leftMatcher, Matcher<T> rightMatcher)
  {
    super(leftMatcher, rightMatcher);
  }

  public boolean match(T object)
  {
    if (leftMatcher.match(object) == false)
      return false;
    return rightMatcher.match(object);
  }

  public String toString()
  {
    return "(" + leftMatcher.toString() + ") && (" + rightMatcher.toString() + ")";
  }

}

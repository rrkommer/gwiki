package de.micromata.genome.util.matcher;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public class OrMatcher<T> extends LeftRightMatcherBase<T>
{

  private static final long serialVersionUID = -1065235686007587539L;

  public OrMatcher(Matcher<T> leftMatcher, Matcher<T> rightMatcher)
  {
    super(leftMatcher, rightMatcher);
  }

  public boolean match(T object)
  {
    if (leftMatcher.match(object) == true)
      return true;
    return rightMatcher.match(object);
  }

  public String toString()
  {
    return "(" + leftMatcher.toString() + ") || (" + rightMatcher.toString() + ")";
  }
}

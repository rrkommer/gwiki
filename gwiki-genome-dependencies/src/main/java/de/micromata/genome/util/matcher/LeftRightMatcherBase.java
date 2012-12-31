package de.micromata.genome.util.matcher;

/**
 * 
 * @author roger
 * 
 */
public abstract class LeftRightMatcherBase<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = 2493922881288900868L;

  protected Matcher<T> leftMatcher;

  protected Matcher<T> rightMatcher;

  public LeftRightMatcherBase()
  {

  }

  public LeftRightMatcherBase(Matcher<T> leftMatcher, Matcher<T> rightMatcher)
  {
    this.leftMatcher = leftMatcher;
    this.rightMatcher = rightMatcher;
  }

  public Matcher<T> getLeftMatcher()
  {
    return leftMatcher;
  }

  public void setLeftMatcher(Matcher<T> leftMatcher)
  {
    this.leftMatcher = leftMatcher;
  }

  public Matcher<T> getRightMatcher()
  {
    return rightMatcher;
  }

  public void setRightMatcher(Matcher<T> rightMatcher)
  {
    this.rightMatcher = rightMatcher;
  }

}

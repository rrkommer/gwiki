package de.micromata.genome.util.matcher;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public class TreeStateMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = -6117823200897120959L;

  private boolean value;

  private Matcher<T> nested;

  public TreeStateMatcher()
  {

  }

  public TreeStateMatcher(Matcher<T> nested, boolean value)
  {
    this.nested = nested;
    this.value = value;
  }

  @Override
  public MatchResult apply(T token)
  {
    MatchResult mr = nested.apply(token);
    if (mr == MatchResult.NoMatch)
      return mr;
    return value ? MatchResult.MatchPositive : MatchResult.MatchNegative;
  }

  public boolean match(T token)
  {
    return apply(token) == MatchResult.MatchPositive;
  }

  public String toString()
  {
    String s = "<EXPR>.match(" + nested.toString() + ")";
    if (value == false)
      s = "!" + s;
    return s;
  }

  public boolean isValue()
  {
    return value;
  }

  public void setValue(boolean value)
  {
    this.value = value;
  }

  public Matcher<T> getNested()
  {
    return nested;
  }

  public void setNested(Matcher<T> nested)
  {
    this.nested = nested;
  }

}

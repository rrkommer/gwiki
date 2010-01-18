package de.micromata.genome.util.matcher;

/**
 * Just a grouped matcher
 * 
 * @author roger@micromata.de
 * 
 */
public class GroupMatcher<T> extends MatcherBase<T>
{
  /**
   * 
   */
  private static final long serialVersionUID = -3472867724633767575L;

  private Matcher<T> groupedMatcher;

  public GroupMatcher()
  {

  }

  public GroupMatcher(Matcher<T> groupedMatcher)
  {
    this.groupedMatcher = groupedMatcher;
  }

  public boolean match(T object)
  {
    return groupedMatcher.match(object);
  }

  public String toString()
  {
    return "(" + groupedMatcher.toString() + ")";
  }
}

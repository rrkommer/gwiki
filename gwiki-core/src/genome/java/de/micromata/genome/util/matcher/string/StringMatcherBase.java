package de.micromata.genome.util.matcher.string;

import de.micromata.genome.util.matcher.MatcherBase;

public abstract class StringMatcherBase<T> extends MatcherBase<T>
{
  /**
   * 
   */
  private static final long serialVersionUID = 6604905652756969768L;

  public abstract boolean matchString(String s);

  public boolean match(T o)
  {
    if ((o instanceof String) == false)
      return false;
    return matchString((String) o);
  }
}

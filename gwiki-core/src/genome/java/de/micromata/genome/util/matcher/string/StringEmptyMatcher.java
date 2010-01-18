package de.micromata.genome.util.matcher.string;

import org.apache.commons.lang.StringUtils;

/**
 * Matches if string is null or empty
 * 
 * @author roger
 * 
 */
public class StringEmptyMatcher<T> extends StringMatcherBase<T>
{

  private static final long serialVersionUID = -6538036307459516611L;

  public StringEmptyMatcher()
  {

  }

  public boolean matchString(String token)
  {
    return StringUtils.isEmpty(token);
  }

  public String toString()
  {
    return "<EXPR>.isEmpty()";
  }
}

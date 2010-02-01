package de.micromata.genome.util.matcher.string;

import org.apache.commons.lang.StringUtils;

/**
 * Matches if string is null or empty
 * 
 * @author roger
 * 
 */
public class StringBlankMatcher<T> extends StringMatcherBase<T>
{
  private static final long serialVersionUID = 7658840012005864847L;

  public StringBlankMatcher()
  {

  }

  public boolean matchString(String token)
  {
    return StringUtils.isBlank(token);
  }

  public String toString()
  {
    return "<EXPR>.isBlank()";
  }
}

package de.micromata.genome.util.matcher.string;

import java.util.regex.Pattern;

/**
 * Matches if regular expression pattern matches
 * 
 * @author roger
 * 
 */
public class RegExpMatcher<T> extends StringMatcherBase<T>
{

  private static final long serialVersionUID = -4826993544156110602L;

  private Pattern pattern;

  public RegExpMatcher()
  {
  }

  public RegExpMatcher(Pattern pattern)
  {
    this.pattern = pattern;

  }

  public RegExpMatcher(String pattern)
  {
    this.pattern = Pattern.compile(pattern);
  }

  public boolean matchString(String token)
  {
    return pattern.matcher(token).matches();
  }

  public String toString()
  {
    return "<EXPR>.regexp(" + pattern.toString() + ")";
  }

  public Pattern getPattern()
  {
    return pattern;
  }

  public void setPattern(Pattern pattern)
  {
    this.pattern = pattern;
  }

}

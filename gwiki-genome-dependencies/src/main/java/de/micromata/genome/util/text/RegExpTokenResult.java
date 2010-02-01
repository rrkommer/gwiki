package de.micromata.genome.util.text;

import java.util.regex.Matcher;

/**
 * 
 * @author roger
 * 
 */
public class RegExpTokenResult extends TokenResultBase
{
  private Matcher regExpMatcher;

  private String matched;

  public RegExpTokenResult(RegExpToken regExpToken, Matcher regExpMatcher, String matched)
  {
    super(regExpToken);
    this.regExpMatcher = regExpMatcher;
    this.matched = matched;
  }

  public String getConsumed()
  {
    return matched;
  }

  public int getConsumedLength()
  {
    int gc = regExpMatcher.groupCount();
    if (gc > 1) {
      return regExpMatcher.start(2);
    }
    return getConsumed().length();
  }

  public Matcher getRegExpMatcher()
  {
    return regExpMatcher;
  }

  public void setRegExpMatcher(Matcher regExpMatcher)
  {
    this.regExpMatcher = regExpMatcher;
  }

  public String toString()
  {

    return regExpMatcher.pattern().toString() + " => '" + matched + "'";
  }
}

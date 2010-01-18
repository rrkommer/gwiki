package de.micromata.genome.util.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author roger
 * 
 */
public class RegExpToken extends TokenBase
{
  private Pattern pattern;

  public RegExpToken(int tokenType, String patternString)
  {
    super(tokenType);
    pattern = Pattern.compile(patternString);
  }

  public boolean match(String text)
  {
    Matcher m = pattern.matcher(text);
    
    boolean doMatch = m.matches() == true;
    return doMatch;
  }

  public TokenResult consume(String text, char escapeChar)
  {
    Matcher matcher = pattern.matcher(text);
    Matcher mr = matcher;// .toMatchResult();
    mr.find();
    if (mr.groupCount() < 2)
      return null;
    String matched = mr.group(1);

    return new RegExpTokenResult(this, matcher, matched);
  }

  public String toString()
  {
    return pattern.toString();
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

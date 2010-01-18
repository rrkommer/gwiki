package de.micromata.genome.util.text;

public class StringToken extends TokenBase
{
  private String pattern;

  public StringToken(int tokenType, String pattern)
  {
    super(tokenType);
    this.pattern = pattern;
  }

  public StringToken()
  {

  }

  public TokenResult consume(String text, char escapeChar)
  {
    if (text.startsWith(pattern) == true)
      return new StringTokenResult(this);

    return null;
  }

  public boolean match(String text)
  {
    return text.startsWith(pattern) == true;
  }

  public String getPattern()
  {
    return pattern;
  }

  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }
}

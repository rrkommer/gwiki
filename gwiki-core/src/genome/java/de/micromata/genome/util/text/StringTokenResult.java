package de.micromata.genome.util.text;

/**
 * 
 * @author roger
 * 
 */
public class StringTokenResult extends TokenResultBase
{
  private String pattern;

  public StringTokenResult(StringToken token)
  {
    super(token);
    this.pattern = token.getPattern();
  }

  public String getConsumed()
  {
    return pattern;
  }

  public int getConsumedLength()
  {
    return pattern.length();
  }
}

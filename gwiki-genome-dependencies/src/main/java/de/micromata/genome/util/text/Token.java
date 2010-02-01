package de.micromata.genome.util.text;

public interface Token
{
  public int getTokenType();

  /**
   * return true if begin of text a token was found
   * 
   * @param text
   * @return
   */
  public boolean match(String text);

  public TokenResult consume(String text, char escapeChar);

}

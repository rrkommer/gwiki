package de.micromata.genome.util.text;

/**
 * 
 * @author roger
 * 
 */
public class CharToken extends TokenBase
{
  private char character;

  public CharToken(int tokenType, char character)
  {
    super(tokenType);
    this.character = character;
  }

  public CharToken()
  {

  }

  public TokenResult consume(String text, char escapeChar)
  {
    if (text.length() > 0 && text.charAt(0) == character) {
      return new CharTokenResult(this);
    }
    return null;
  }

  public boolean match(String text)
  {
    return text.length() > 0 && text.charAt(0) == character;
  }

  public char getCharacter()
  {
    return character;
  }

  public void setCharacter(char character)
  {
    this.character = character;
  }

}

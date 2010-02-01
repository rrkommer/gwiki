package de.micromata.genome.util.text;

public class CharTokenResult extends TokenResultBase
{
  private char character;

  public CharTokenResult(CharToken token)
  {
    super(token);
    this.character = token.getCharacter();
  }

  public String getConsumed()
  {
    return Character.toString(character);
  }

  public int getConsumedLength()
  {
    return 1;
  }

}

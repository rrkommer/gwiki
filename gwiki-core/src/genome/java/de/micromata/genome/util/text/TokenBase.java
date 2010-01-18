package de.micromata.genome.util.text;

public abstract class TokenBase implements Token
{
  private int tokenType;

  public TokenBase()
  {

  }

  public TokenBase(int tokenType)
  {
    this.tokenType = tokenType;
  }

  public int getTokenType()
  {
    return tokenType;
  }

  public void setTokenType(int tokenType)
  {
    this.tokenType = tokenType;
  }

}

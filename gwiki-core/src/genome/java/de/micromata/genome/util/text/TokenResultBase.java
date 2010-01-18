package de.micromata.genome.util.text;

public abstract class TokenResultBase implements TokenResult
{
  private Token token;

  public TokenResultBase(Token token)
  {
    this.token = token;
  }

  public int getTokenType()
  {
    return token.getTokenType();
  }

  public Token getToken()
  {
    return token;
  }

  public void setToken(Token token)
  {
    this.token = token;
  }

}

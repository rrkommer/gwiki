package de.micromata.genome.util.text;

public interface TokenResult
{
  public int getTokenType();

  public String getConsumed();

  public int getConsumedLength();

}

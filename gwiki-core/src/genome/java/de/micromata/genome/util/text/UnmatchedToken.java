package de.micromata.genome.util.text;

public class UnmatchedToken implements TokenResult
{
  private String unmatched;

  public UnmatchedToken(String unmatched)
  {
    this.unmatched = unmatched;
  }

  public String toString()
  {
    return unmatched;
  }

  public int getTokenType()
  {
    return 0;
  }

  public String getConsumed()
  {
    return unmatched;
  }

  public int getConsumedLength()
  {
    return unmatched.length();
  }

  public String getUnmatched()
  {
    return unmatched;
  }

  public void setUnmatched(String unmatched)
  {
    this.unmatched = unmatched;
  }

}

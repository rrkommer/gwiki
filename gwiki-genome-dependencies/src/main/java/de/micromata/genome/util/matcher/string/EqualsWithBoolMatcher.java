package de.micromata.genome.util.matcher.string;

/**
 * Matches if pattern is equals to token.
 * 
 * if token is false, also accept null values
 * 
 * @author roger
 * 
 */
public class EqualsWithBoolMatcher extends StringPatternMatcherBase<String>
{

  private static final long serialVersionUID = -531763040829078392L;

  public EqualsWithBoolMatcher()
  {

  }

  public EqualsWithBoolMatcher(String other)
  {
    super(other);
  }

  public boolean match(String o)
  {
    return matchString((String) o);

  }

  public boolean matchString(String token)
  {
    if (token == null && pattern != null) {
      if (pattern.equalsIgnoreCase("false") == true) {
        return true;
      }
      return false;
    }
    if (token != null && pattern == null) {
      if (token.equalsIgnoreCase("false") == false) {
        return true;
      }
      return false;
    }
    return token.equals(pattern);
  }

  public String toString()
  {
    return pattern.toString() + " = <ExprWithBool>";
  }
}

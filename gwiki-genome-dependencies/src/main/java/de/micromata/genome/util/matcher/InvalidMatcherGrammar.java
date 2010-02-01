package de.micromata.genome.util.matcher;

/**
 * 
 * @author roger
 * 
 */
public class InvalidMatcherGrammar extends RuntimeException
{

  private static final long serialVersionUID = 2572461712815733388L;

  public InvalidMatcherGrammar()
  {
    super();
  }

  public InvalidMatcherGrammar(String message, Throwable cause)
  {
    super(message, cause);
  }

  public InvalidMatcherGrammar(String message)
  {
    super(message);
  }

  public InvalidMatcherGrammar(Throwable cause)
  {
    super(cause);
  }

}

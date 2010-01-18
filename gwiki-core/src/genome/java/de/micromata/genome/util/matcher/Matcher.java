package de.micromata.genome.util.matcher;

import java.io.Serializable;
import java.util.Collection;

/**
 * Base class for matching
 * 
 * @author roger
 * 
 */
public interface Matcher<T> extends Serializable
{

  /**
   * Allgemeine Methode um ein Object auf das "Passen" zu überpüfen
   * 
   * @param object Das zu checkende Objekt
   * @return true, falls das Objekt passt
   */
  public boolean match(T object);

  /**
   * similar to match, but return 3 state.
   * 
   * @param object
   * @return
   */
  public MatchResult apply(T object);

  /**
   * 
   * @param sl
   * @param defaultValue if none are matched, returns defualtValue
   * @return
   */
  boolean matchAny(Collection<T> sl, boolean defaultValue);

  boolean matchAll(Collection<T> sl, boolean defaultValue);
}

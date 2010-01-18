package de.micromata.genome.util.matcher;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public class GroovyMatcherFactory<T> implements MatcherFactory<T>
{

  public Matcher<T> createMatcher(String pattern)
  {
    GroovyShell gs = new GroovyShell();
    Script script = gs.parse(pattern);
    return new GroovyMatcher<T>(pattern, script.getClass());
  }

  public String getRuleString(Matcher<T> matcher)
  {
    if ((matcher instanceof GroovyMatcher) == false)
      return "<unknown>";
    return "${" + ((GroovyMatcher<T>) matcher).getSource() + "}";
  }

}

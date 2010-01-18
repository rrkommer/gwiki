/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.09.2008
// Copyright Micromata 21.09.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher;

import de.micromata.genome.util.matcher.string.ContainsMatcher;
import de.micromata.genome.util.matcher.string.EndsWithMatcher;
import de.micromata.genome.util.matcher.string.RegExpMatcher;
import de.micromata.genome.util.matcher.string.StartWithMatcher;
import de.micromata.genome.util.matcher.string.WildcardMatcher;

/**
 * 
 * @author roger@micromata.de
 * @deprecated use CommonMatcherBuilder
 */
@Deprecated
public class Matchers
{
  public static <T> Matcher<T> equal(T obj)
  {
    return new EqualsMatcher<T>(obj);
  }

  public static Matcher<String> containsString(String obj)
  {
    return new ContainsMatcher<String>(obj);
  }

  public static Matcher<String> startsWith(String o)
  {
    return new StartWithMatcher<String>(o);
  }

  public static Matcher<String> endsWith(String o)
  {
    return new EndsWithMatcher<String>(o);
  }

  public static Matcher<String> reqexp(String obj)
  {
    return new RegExpMatcher<String>(obj);
  }

  public static Matcher<String> wildcart(String obj)
  {
    return new WildcardMatcher<String>(obj);
  }

  public <T> Matcher<T> and(Matcher<T> first, Matcher<T> second)
  {
    return new AndMatcher<T>(first, second);
  }

  public <T> Matcher<T> or(Matcher<T> first, Matcher<T> second)
  {
    return new OrMatcher<T>(first, second);
  }

  public Matcher<Object> instanceOf(Class< ? > cls)
  {
    return new BeanInstanceOfMatcher(cls);
  }

  public <T> Matcher<T> not(Matcher<T> m)
  {
    return new NotMatcher<T>(m);
  }
  // TODO (minor) to be continued
}

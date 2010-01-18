/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   04.07.2009
// Copyright Micromata 04.07.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher;

/**
 * Builder static methods.
 * 
 * @author roger@micromata.de
 * 
 */
public class CommonMatcherBuilder
{
  public static <T> Matcher<T> or(Matcher<T> left, Matcher<T> right)
  {
    return new OrMatcher<T>(left, right);
  }

  public static <T> Matcher<T> or(Matcher<T>... elements)
  {
    if (elements.length == 0)
      throw new RuntimeException("CommonMatcherBuilder.or needs at least one arg");
    if (elements.length == 1)
      return elements[0];
    return new OrMatcher<T>(elements[0], or(1, elements));
  }

  public static <T> Matcher<T> or(int offset, Matcher<T>... elements)
  {
    if (elements.length == offset)
      throw new RuntimeException("CommonMatcherBuilder.and needs at least one arg");
    if (elements.length - offset == 1)
      return elements[offset];
    return new OrMatcher<T>(elements[offset], or(++offset, elements));
  }

  public static <T> Matcher<T> and(Matcher<T>... elements)
  {
    if (elements.length == 0)
      throw new RuntimeException("CommonMatcherBuilder.and needs at least one arg");
    if (elements.length == 1)
      return elements[0];
    return new AndMatcher<T>(elements[0], and(1, elements));
  }

  public static <T> Matcher<T> and(int offset, Matcher<T>... elements)
  {
    if (elements.length == offset)
      throw new RuntimeException("CommonMatcherBuilder.and needs at least one arg");
    if (elements.length - offset == 1)
      return elements[offset];
    return new AndMatcher<T>(elements[offset], and(++offset, elements));
  }

  public static <T> Matcher<T> not(Matcher<T> nested)
  {
    return new NotMatcher<T>(nested);
  }

  public static <T> Matcher<T> group(Matcher<T> nested)
  {
    return new GroupMatcher<T>(nested);
  }

  public static <T> Matcher<T> equals(T other)
  {
    return new EqualsMatcher<T>(other);
  }

  public static <T> Matcher<T> never()
  {
    return new NoneMatcher<T>();
  }

  public static <T> Matcher<T> always()
  {
    return new EveryMatcher<T>();
  }
}

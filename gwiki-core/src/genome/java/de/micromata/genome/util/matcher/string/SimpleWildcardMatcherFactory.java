package de.micromata.genome.util.matcher.string;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.matcher.EveryMatcher;
import de.micromata.genome.util.matcher.GroovyMatcher;
import de.micromata.genome.util.matcher.GroovyMatcherFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherFactory;
import de.micromata.genome.util.matcher.NotMatcher;
import de.micromata.genome.util.text.TextSplitterUtils;

/**
 * Creates a Wildcard Matcher with some improvements.
 * 
 * In case of simple pattern it creates optimized Matcher
 * <p/>
 * <ul>
 * <li>: EveryMatcher
 * <li>string: EqualsMatcher
 * <li>string*: StartWithMatcher
 * <li>string: EndsWithMatcher
 * <li>string*: ContainsMatcher
 * <li>string*string: WildcardMatcher
 * <li>contains ?: WildcardMatcher
 * </ul>
 * 
 * 
 * @author roger
 * 
 */
public class SimpleWildcardMatcherFactory<T> implements MatcherFactory<T>
{
  private char escapeChar = '\\';

  // Note this creates an anonous class, which is wanted! don't delete {}!
  private final static SimpleWildcardMatcherFactory<String> defaultImpl = new SimpleWildcardMatcherFactory<String>() {};

  public static SimpleWildcardMatcherFactory<String> getDefaultImpl()
  {
    return defaultImpl;
  }

  protected List<Integer> findTokens(String pattern, char tk)
  {
    List<Integer> ret = new ArrayList<Integer>();
    for (int i = 0; i < pattern.length(); ++i) {
      if (pattern.charAt(i) == tk)
        ret.add(i);
    }
    return ret;
  }

  protected Matcher<T> createWildcartMatcher(String pattern)
  {
    if (TextSplitterUtils.getUnescapedIndexOf(pattern, '?', escapeChar) != -1)
      return new WildcardMatcher<T>(pattern);

    if (pattern.length() == 1 && pattern.charAt(0) == '*')
      return new EveryMatcher<T>();

    List<Integer> positions = TextSplitterUtils.findTokenPos(pattern, '*', escapeChar);
    String upattern = TextSplitterUtils.unescape(pattern, escapeChar, '?', '*');
    // List<Integer> positions = findTokens(pattern, '*');
    if (positions.size() == 0)
      return new EqualsMatcher<T>((T) (Object) upattern);

    if (positions.size() == 1) {
      if (positions.get(0) == 0)
        return new EndsWithMatcher<T>(upattern.substring(1));

      if (positions.get(0) == pattern.length() - 1)
        return new StartWithMatcher<T>(upattern.substring(0, upattern.length() - 1));
    }

    if (positions.size() > 2)
      return new WildcardMatcher<T>(upattern);

    if (positions.size() == 2) {
      if (positions.get(0) == 0 && positions.get(1) == pattern.length() - 1)
        return new ContainsMatcher<T>(upattern.substring(1, upattern.length() - 1));
      return new WildcardMatcher<T>(upattern);
    }

    return new WildcardMatcher<T>(upattern);
  }

  public Matcher<T> createMatcher(String pattern)
  {

    if (pattern.startsWith("!") == true) {
      return new NotMatcher<T>(createMatcher(pattern.substring(1)));
    }
    if (pattern.startsWith("~") == true) {
      return new RegExpMatcherFactory<T>().createMatcher(pattern.substring(1));
    }
    if (pattern.startsWith("${") == true && pattern.endsWith("}") == true)
      return new GroovyMatcherFactory<T>().createMatcher(pattern.substring(2, pattern.length() - 1));

    return createWildcartMatcher(pattern);
  }

  public String getRuleString(Matcher<T> matcher)
  {
    if (matcher instanceof EveryMatcher)
      return "*";
    if (matcher instanceof GroovyMatcher) {
      return new GroovyMatcherFactory<T>().getRuleString(matcher);
    }
    if (matcher instanceof RegExpMatcher) {
      return "~" + new RegExpMatcherFactory<T>().getRuleString(matcher);
    }
    if (matcher instanceof NotMatcher) {
      return "!" + getRuleString(((NotMatcher<T>) matcher).getNested());
    }
    String s = getWildcartRuleString(matcher);
    return s;// TextSplitterUtils.escape(s, escapeChar, '?', '*');
  }

  public String getWildcartRuleString(Matcher<T> matcher)
  {
    if (matcher instanceof EqualsMatcher)
      return ((EqualsMatcher<T>) matcher).getOther().toString();
    if (matcher instanceof WildcardMatcher)
      return ((WildcardMatcher<T>) matcher).getPattern();
    if (matcher instanceof ContainsMatcher)
      return "*" + ((ContainsMatcher<T>) matcher).getPattern() + "*";
    if (matcher instanceof EndsWithMatcher)
      return "*" + ((EndsWithMatcher<T>) matcher).getPattern();
    if (matcher instanceof StartWithMatcher)
      return ((StartWithMatcher<T>) matcher).getPattern() + "*";

    return "<unkown>";
  }

  public char getEscapeChar()
  {
    return escapeChar;
  }

  public void setEscapeChar(char escapeChar)
  {
    this.escapeChar = escapeChar;
  }
}

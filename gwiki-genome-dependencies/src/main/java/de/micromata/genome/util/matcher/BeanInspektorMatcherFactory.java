package de.micromata.genome.util.matcher;

import org.apache.commons.lang.StringUtils;

public class BeanInspektorMatcherFactory implements MatcherFactory<Object>
{

  public Matcher<Object> createMatcher(String pattern)
  {
    String matcherString = StringUtils.trim(StringUtils.substringBefore(pattern, "="));
    String valueString = StringUtils.trimToNull(StringUtils.substringAfter(pattern, "="));

    if (matcherString.trim().equals("instanceOf")) {
      try {
        return new BeanInstanceOfMatcher(Class.forName(valueString.trim()));
      } catch (Exception ex) {
        throw new RuntimeException(ex); // TODO better ex
      }
    }
    return new BeanPropertiesMatcher(matcherString, valueString);
  }

  public String getRuleString(Matcher<Object> matcher)
  {
    if (matcher instanceof BeanInstanceOfMatcher)
      return "instanceOf=" + ((BeanInstanceOfMatcher) matcher).getOfClass();
    if (matcher instanceof BeanPropertiesMatcher) {
      BeanPropertiesMatcher bpm = ((BeanPropertiesMatcher) matcher);
      return bpm.getProperty() + "=" + bpm.getValue();
    }
    return "<unknown>";
  }
}

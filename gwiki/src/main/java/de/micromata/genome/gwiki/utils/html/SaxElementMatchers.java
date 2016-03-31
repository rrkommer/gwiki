package de.micromata.genome.gwiki.utils.html;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherBase;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class SaxElementMatchers
{
  public static Matcher<SaxElement> nameMatcher(String name)
  {
    return new MatcherBase<SaxElement>()
    {

      @Override
      public boolean match(SaxElement object)
      {
        return object.getElementName().equals(name);
      }

    };
  }

  public static Matcher<SaxElement> attribute(String name, Matcher<String> attr)
  {
    return new MatcherBase<SaxElement>()
    {

      @Override
      public boolean match(SaxElement object)
      {
        String attrv = object.getAttributes().getValue(name);
        if (attrv == null) {
          return false;
        }
        return attr.match(attrv);
      }

    };
  }

  public static Matcher<SaxElement> withBody(boolean withBody)
  {
    return new MatcherBase<SaxElement>()
    {

      @Override
      public boolean match(SaxElement object)
      {
        return withBody == object.hasBody;
      }
    };
  }
}

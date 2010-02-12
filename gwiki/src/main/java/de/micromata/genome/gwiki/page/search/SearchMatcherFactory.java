/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   29.10.2009
// Copyright Micromata 29.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import java.util.List;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherFactory;
import de.micromata.genome.util.matcher.OrMatcher;
import de.micromata.genome.util.matcher.string.ContainsMatcher;
import de.micromata.genome.util.types.Converter;

/**
 * Factory to create matchers.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SearchMatcherFactory implements MatcherFactory<String>
{

  public Matcher<String> createMatcher(String pattern)
  {
    List<String> parts = Converter.parseStringTokens(pattern, " ", false);

    Matcher<String> lm = null;// new OrMatcher<String>();

    for (String p : parts) {
      if (lm == null) {
        lm = new ContainsMatcher<String>(p);
      } else {
        lm = new OrMatcher<String>(lm, new ContainsMatcher<String>(p));
      }
    }
    return lm;
  }

  public String getRuleString(Matcher<String> matcher)
  {
    return "not supported";
  }

}

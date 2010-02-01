/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   04.07.2009
// Copyright Micromata 04.07.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher.cls;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherBase;

/**
 * Matches agains the canonical class name.
 * 
 * @author roger@micromata.de
 * 
 */
public class ClassNameMatcher extends MatcherBase<Class< ? >>
{

  private static final long serialVersionUID = 1428257782512889135L;

  private Matcher<String> nameMatcher;

  public ClassNameMatcher(Matcher<String> nameMatcher)
  {
    this.nameMatcher = nameMatcher;
  }

  public boolean match(Class< ? > cls)
  {
    if (cls == null)
      return false;
    final String cn = cls.getCanonicalName();
    return nameMatcher.match(cn);
  }

  public String toString()
  {
    return "<EXPR>.matchClassName(" + nameMatcher.toString() + ")";
  }

  public Matcher<String> getNameMatcher()
  {
    return nameMatcher;
  }

}

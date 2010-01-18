/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostageVoucher
//
// Author    jensi@micromata.de
// Created   27.02.2009
// Copyright Micromata 27.02.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher.name;

import java.util.ArrayList;
import java.util.Arrays;

import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.types.Name;

/**
 * matches if at least one given name has equal name() value.
 * 
 * @author jens@micromata.de
 */
public class AnyNameMatcher<T extends Name> extends MatcherBase<T>
{
  private static final long serialVersionUID = 5697802074293705812L;

  private Iterable< ? extends Name> validNames;

  
  public AnyNameMatcher(Name... validNames)
  {
    this(Arrays.asList(validNames));
  }
  
  public AnyNameMatcher(Iterable< ? extends Name> validNames)
  {
    if (validNames == null) {
      this.validNames = new ArrayList<Name>(0);
    }
    this.validNames = validNames;
  }

  public boolean match(T name)
  {
    for (Name n : validNames) {
      if (n.name().equals(name.name()) == true) {
        return true;
      }
    }
    return false;
  }

  public static <T extends Name> AnyNameMatcher<T> getInstance(T... names)
  {
    return new AnyNameMatcher<T>(names);
  }

}

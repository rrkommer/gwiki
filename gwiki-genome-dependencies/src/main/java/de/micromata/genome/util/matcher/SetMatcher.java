/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   31.12.2008
// Copyright Micromata 31.12.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher;

import java.util.Set;

import org.apache.commons.lang.Validate;

/**
 * Matches agains a set via contains
 * 
 * @author roger@micromata.de
 * 
 */
public class SetMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = -870906355711283761L;

  private Set<T> set;

  public SetMatcher(Set<T> set)
  {
    Validate.notNull(set);
    this.set = set;
  }

  public boolean match(T object)
  {
    return set.contains(object);
  }

}

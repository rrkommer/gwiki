/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.11.2009
// Copyright Micromata 14.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher;

/**
 * Base class for comparator matchers.
 * 
 * Match alwyas return true if one argument is null.
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class ComparatorMatcherBase<T extends Comparable<T>> extends MatcherBase<T>
{

  private static final long serialVersionUID = -5533194814723147344L;

  protected T other;

  public ComparatorMatcherBase()
  {

  }

  public ComparatorMatcherBase(T other)
  {
    this.other = other;
  }

  public T getOther()
  {
    return other;
  }

  public void setOther(T other)
  {
    this.other = other;
  }

}

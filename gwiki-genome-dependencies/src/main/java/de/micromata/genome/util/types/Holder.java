/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.02.2007
// Copyright Micromata 09.02.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

/**
 * Indirection to hold value. May be used where local variable has to be final.
 * 
 * @author roger@micromata.de
 * 
 */
public class Holder<T>
{

  private T holded;

  public Holder()
  {
  }

  public Holder(T t)
  {
    holded = t;
  }

  public T get()
  {
    return holded;
  }

  public void set(T t)
  {
    holded = t;
  }

  public T getHolded()
  {
    return holded;
  }

  public void setHolded(T t)
  {
    holded = t;
  }
}

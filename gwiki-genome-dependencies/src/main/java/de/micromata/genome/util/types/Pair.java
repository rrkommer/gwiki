/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.07.2006
// Copyright Micromata 09.07.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

/**
 * Similar to c++ type pair.
 * 
 * @author roger@micromata.de
 * 
 */
public class Pair<K, V> implements Map.Entry<K, V>, Serializable
{
  private static final long serialVersionUID = 1427196812388547552L;

  private K key;

  private V value;

  public static <MK, MV> Pair<MK, MV> make(MK key, MV value)
  {
    return new Pair<MK, MV>(key, value);
  }

  public Pair()
  {
  }

  public Pair(K key, V value)
  {
    this.key = key;
    this.value = value;
  }

  public K getKey()
  {
    return key;
  }

  public void setKey(K key)
  {
    this.key = key;
  }

  public V getValue()
  {
    return value;
  }

  public V setValue(V value)
  {
    V t = this.value;
    this.value = value;
    return t;
  }

  public K getFirst()
  {
    return key;
  }

  public V getSecond()
  {
    return value;
  }

  public void setFirst(K k)
  {
    key = k;
  }

  public void setSecond(V v)
  {
    value = v;
  }

  public String toString()
  {
    return key + ": " + value;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Pair) {
      Pair< ? , ? > other = (Pair< ? , ? >) obj;
      return ObjectUtils.equals(key, other.key) && ObjectUtils.equals(value, other.value);

    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtils.hashCode(key) * 31 + ObjectUtils.hashCode(value);
  }
}

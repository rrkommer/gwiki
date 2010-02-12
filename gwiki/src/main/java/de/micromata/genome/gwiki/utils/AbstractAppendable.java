/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.12.2009
// Copyright Micromata 02.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils;

/**
 * Appendable, which does not throw IOExceptions.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class AbstractAppendable implements AppendableI
{

  public AppendableI append(CharSequence csq, int start, int end)
  {
    return append(csq.subSequence(start, end));
  }

  public AppendableI append(char c)
  {
    return append(Character.toString(c));
  }

  public AppendableI append(Object value)
  {
    return append(String.valueOf(value));
  }

  public AppendableI append(Object... values)
  {
    for (Object val : values) {
      append(val);
    }
    return this;
  }

  public AppendableI append(CharSequence s)
  {
    if (s == null) {
      s = "null";
    }
    if (s instanceof String)
      return this.append((String) s);
    if (s instanceof StringBuffer)
      return this.append((StringBuffer) s);
    return this.append(s.toString());
  }
}

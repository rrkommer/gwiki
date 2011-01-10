/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.07.2006
// Copyright Micromata 03.07.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.logging;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import de.micromata.genome.util.types.Converter;

/**
 * 
 * @author roger@micromata.de
 * 
 */
public class LogAttribute implements Serializable
{

  private static final long serialVersionUID = 4447381062765214393L;

  private LogAttributeType type;

  private String value;

  protected LogAttribute()
  {
  }

  public LogAttribute(LogAttributeType type, String value)
  {
    Validate.notNull(type);
    this.type = type;
    this.value = value;
  }

  public final boolean isSearchKey()
  {
    return type.isSearchKey();
  }

  public static String shorten(String val, int size)
  {
    if (val == null)
      return StringUtils.EMPTY;
    return Converter.trimUtf8(val, size);
  }

  public LogAttributeType getType()
  {
    return type;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtils.hashCode(type) * 17 + ObjectUtils.hashCode(value);
  }

  /**
   * Prueft nur ob type identisch ist.
   */
  @Override
  public boolean equals(Object other)
  {
    if ((other instanceof LogAttribute) == false)
      return false;
    LogAttribute lo = (LogAttribute) other;
    return type == lo.getType();
  }

  public final void setType(LogAttributeType type)
  {
    this.type = type;
  }

  public final String getTypeName()
  {
    return type.name();
  }

  public String getValue()
  {
    return value;
  }

  public String getEscapedValue()
  {
    return StringEscapeUtils.escapeXml(value);
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  /**
   * helper to render a map to a string
   */
  public static String getRequestDump(Map<String, String> reqMap)
  {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> e : reqMap.entrySet()) {
      sb.append("|").append(e.getKey()).append("=").append(e.getValue()).append("\n");
    }
    return sb.toString();
  }

}

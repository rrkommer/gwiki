/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   16.02.2007
// Copyright Micromata 16.02.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi.jdbc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class BaseCreatableDO
{
  private static final SimpleDateFormat isoTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

  public static String formatDate(Date date)
  {
    synchronized (isoTimestampFormat) {
      return isoTimestampFormat.format(date);
    }
  }

  private String createdBy;

  private Date createdAt;

  public Date getCurrentTime()
  {
    return new Date();
  }

  public Date getCreatedAt()
  {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt)
  {
    this.createdAt = createdAt;
  }

  public String getCreatedBy()
  {
    return createdBy;
  }

  public void setCreatedBy(String createdBy)
  {
    this.createdBy = createdBy;
  }

  public String getCreatedAtString()
  {
    if (createdAt == null)
      return null;
    return formatDate(createdAt);
  }

  public void toString(ToStringBuilder sb)
  {
    sb.append("createdAt", getCreatedAtString());
  }
}

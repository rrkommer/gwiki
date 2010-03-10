/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   16.02.2008
// Copyright Micromata 16.02.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.dao.db;

import java.io.Serializable;
import java.util.Date;

import de.micromata.genome.util.types.Converter;

/**
 * 
 * @author roger@micromata.de
 * 
 */
public class StdRecordDO implements Serializable
{

  private static final long serialVersionUID = 3891095023083295900L;

  protected Long pk;

  private String createdBy;

  private String modifiedBy;

  private Date modifiedAt;

  private Date createdAt;

  private Integer updateCounter;

  public StdRecordDO()
  {

  }

  public StdRecordDO(StdRecordDO other)
  {
    this.pk = other.pk;
    this.createdAt = other.createdAt;
    this.createdBy = other.createdBy;
    this.modifiedAt = other.modifiedAt;
    this.modifiedBy = other.modifiedBy;
    this.updateCounter = other.updateCounter;
  }

  public String getModifiedBy()
  {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy)
  {
    this.modifiedBy = modifiedBy;
  }

  public Date getModifiedAt()
  {
    return modifiedAt;
  }

  public String getModifiedAtString()
  {
    if (modifiedAt == null)
      return "";
    return Converter.formatByIsoDateFormat(modifiedAt);
  }

  public void setModifiedAt(Date modifiedAt)
  {
    this.modifiedAt = modifiedAt;
  }

  public void setModifiedAtString(String str)
  {
    this.modifiedAt = Converter.parseIsoDateToDate(str);
  }

  public Date getCreatedAt()
  {
    return createdAt;
  }

  public String getCreatedAtString()
  {
    if (createdAt == null)
      return "";
    return Converter.formatByIsoDateFormat(createdAt);
  }

  public void setCreatedAtString(String str)
  {
    createdAt = Converter.parseIsoDateToDate(str);
  }

  public void setCreatedAt(Date createdAt)
  {
    this.createdAt = createdAt;
  }

  // Für optimistic locking
  public Integer getUpdateCounter()
  {
    return updateCounter;
  }

  public void setUpdateCounter(Integer updateCounter)
  {
    this.updateCounter = updateCounter;
  }

  public Long getPk()
  {
    return pk;
  }

  public void setPk(Long pk)
  {
    this.pk = pk;
  }

  public String getCreatedBy()
  {
    return createdBy;
  }

  public void setCreatedBy(String createdBy)
  {
    this.createdBy = createdBy;
  }
  // compat < R3
  @Deprecated
  public long getId()
  {
    return getPk();
  }
}

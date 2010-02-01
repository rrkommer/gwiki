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

public class FieldTypeDO implements FieldType
{
  private String name;

  private int sqlType;

  private int maxSize;

  private boolean unicode;

  public FieldTypeDO()
  {

  }

  public FieldTypeDO(String name, int sqlType, int maxSize, boolean unicode)
  {
    this.name = name;
    this.sqlType = sqlType;
    this.maxSize = maxSize;
    this.unicode = unicode;
  }

  public FieldTypeDO(String name, StdMetaFieldTypes other)
  {
    this.name = name;
    this.sqlType = other.getSqlType();
    this.maxSize = other.getMaxSize();
    this.unicode = other.isUnicode();
  }

  public FieldTypeDO(FieldType other)
  {
    this.name = other.name();
    this.sqlType = other.getSqlType();
    this.maxSize = other.getMaxSize();
    this.unicode = other.isUnicode();
  }

  public int getMaxSize()
  {
    return maxSize;
  }

  public int getSqlType()
  {
    return sqlType;
  }

  public boolean isUnicode()
  {
    return unicode;
  }

  public String name()
  {
    return name;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setSqlType(int sqlType)
  {
    this.sqlType = sqlType;
  }

  public void setMaxSize(int maxSize)
  {
    this.maxSize = maxSize;
  }

  public void setUnicode(boolean unicode)
  {
    this.unicode = unicode;
  }

}

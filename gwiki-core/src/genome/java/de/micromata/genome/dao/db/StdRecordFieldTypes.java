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

/**
 * 
 * @author roger@micromata.de
 * 
 */
public enum StdRecordFieldTypes implements FieldType
{
  CREATEDAT(StdMetaFieldTypes.TIMESTAMP), //
  MODIFIEDAT(StdMetaFieldTypes.TIMESTAMP), //
  CREATEDBY(StdMetaFieldTypes.SYMBOL), //
  MODIFIEDBY(StdMetaFieldTypes.SYMBOL), //
  UPDATECOUNTER(StdMetaFieldTypes.COUNTER) //
  ;
  private FieldType fieldType;

  private StdRecordFieldTypes(FieldType type)
  {
    this.fieldType = type;
  }

  private StdRecordFieldTypes(StdMetaFieldTypes metaType)
  {
    this.fieldType = new FieldTypeDO(name(), metaType);
  }

  private StdRecordFieldTypes(StdMetaFieldTypes metaType, int maxLength)
  {
    FieldTypeDO ft = new FieldTypeDO(name(), metaType);
    ft.setMaxSize(maxLength);
    this.fieldType = ft;
  }

  public int getMaxSize()
  {
    return fieldType.getMaxSize();
  }

  public int getSqlType()
  {
    return fieldType.getSqlType();
  }

  public boolean isUnicode()
  {
    return fieldType.isUnicode();
  }
}

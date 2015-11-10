////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////

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

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

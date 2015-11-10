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

/*
 Created on 09.01.2008
 */
package de.micromata.genome.db.spring;

public class SimpleDBTable implements DBTable
{
  private String tableName;

  private String pkColumnName;

  private String pkSequenceName;

  public SimpleDBTable()
  {

  }

  public SimpleDBTable(String tableName)
  {
    this(tableName, null);
  }

  public SimpleDBTable(String tableName, String pkName)
  {
    this(tableName, pkName, null);
  }

  public SimpleDBTable(String tableName, String pkName, String pkSequenceName)
  {
    this.tableName = tableName;
    this.pkColumnName = pkName;
    this.pkSequenceName = pkSequenceName;
  }

  public String getPkColumnName()
  {
    return pkColumnName;
  }

  public String getTableName()
  {
    return tableName;
  }

  public void setPkColumnName(String pkColumnName)
  {
    this.pkColumnName = pkColumnName;
  }

  public void setTableName(String tableName)
  {
    this.tableName = tableName;
  }

  public String getPkSequenceName()
  {
    return pkSequenceName;
  }

  public void setPkSequenceName(String pkSequenceName)
  {
    this.pkSequenceName = pkSequenceName;
  }

}

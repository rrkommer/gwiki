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
// Author    michael@micromata.de
// Created   Feb 19, 2008
// Copyright Micromata Feb 19, 2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.dao.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Hilfs Klasse für RowMapper bei StdRecordDO abgleitete Objekte Nicht selber ein RowMapper weil ein RowMapper des komplete Objekt erzeugt
 * 
 * @author michael@micromata.de
 */
public abstract class StdRecordRowMapper
{
  private String pkColumnName;

  public StdRecordRowMapper(String pkColumnName)
  {
    this.pkColumnName = pkColumnName;
  }

  public void mapStdRecordRows(StdRecordDO rdo, ResultSet rs) throws SQLException
  {
    rdo.setPk(rs.getLong(pkColumnName));
    rdo.setCreatedAt(DbFieldUtils.readDate(StdRecordFieldTypes.CREATEDAT, rs));
    rdo.setCreatedBy(DbFieldUtils.readString(StdRecordFieldTypes.CREATEDBY, rs));
    rdo.setModifiedAt(DbFieldUtils.readDate(StdRecordFieldTypes.MODIFIEDAT, rs));
    rdo.setModifiedBy(DbFieldUtils.readString(StdRecordFieldTypes.MODIFIEDBY, rs));
    rdo.setUpdateCounter(DbFieldUtils.readInteger(StdRecordFieldTypes.UPDATECOUNTER, rs));
  }

}

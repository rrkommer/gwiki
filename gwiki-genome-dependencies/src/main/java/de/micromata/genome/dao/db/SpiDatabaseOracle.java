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

package de.micromata.genome.dao.db;

/**
 * SpiDatabase for Oracle
 * 
 * @author roger
 * 
 */
public class SpiDatabaseOracle implements SpiDatabase
{
  /**
   * @see de.micromata.genome.dao.db.SpiDatabase.getInPlaceSequenceSelect(String)
   */
  public String getInPlaceSequenceSelect(String sequenceName)
  {
    return sequenceName + ".nextVal";
  }

  public String getNowTimestamp()
  {
    return " SYSTIMESTAMP ";
  }

  public String getSequenceSelect(String sequenceName)
  {
    return "select " + sequenceName + ".nextVal from DUAL";
  }

  public boolean supportUniqConstraintsWithNulls()
  {
    return true;
  }

  public String createSelectWithLimit(String limit, String selects, String where, String order)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("select ").append(selects);
    if (where != null) {
      sb.append(where);
      sb.append(" AND ROWNUM &lt;= ").append(limit);
    } else {
      sb.append(" WHERE ROWNUM &lt;= ").append(limit);
    }
    if (order != null)
      sb.append(order);
    return sb.toString();
  }
}

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
 * SpiDatabase for PostgreSQL
 * 
 * @author roger
 * 
 */
public class SpiDatabasePostgres implements SpiDatabase
{
  public String getInPlaceSequenceSelect(String sequenceName)
  {
    return "nextval('" + sequenceName + "')";
  }

  public String getNowTimestamp()
  {
    return " 'now' ";
  }

  public String getSequenceSelect(String sequenceName)
  {
    return "select nextval('" + sequenceName + "')";
  }

  public boolean supportUniqConstraintsWithNulls()
  {
    return false;
  }

  /**
   * select * from (select * from &CHRONOS_RESULT_TABLE; order by CREATEDAT) where TA_CHRONOS_JOB = #param# LIMIT 1000
   * 
   * @param limit
   * @param selects
   * @param where
   * @return
   */
  public String createSelectWithLimit(String limit, String selects, String where, String order)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("select ").append(selects);
    if (where != null)
      sb.append(where);
    if (order != null)
      sb.append(order);
    sb.append(" LIMIT ").append(limit);
    return sb.toString();
  }
}

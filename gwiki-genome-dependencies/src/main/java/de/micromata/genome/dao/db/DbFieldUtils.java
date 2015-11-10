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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DbFieldUtils
{
  public static String readString(FieldType type, ResultSet rs) throws SQLException
  {
    return rs.getString(type.name());
  }

  public static Timestamp readTimestamp(String colname, ResultSet rs) throws SQLException
  {
    return rs.getTimestamp(colname);
  }

  public static Timestamp readTimestamp(FieldType type, ResultSet rs) throws SQLException
  {
    return readTimestamp(type.name(), rs);
  }

  public static Date readDate(FieldType type, ResultSet rs) throws SQLException
  {
    Timestamp ts = rs.getTimestamp(type.name());
    if (ts == null)
      return null;
    return new Date(ts.getTime());
  }

  public static Long readLong(FieldType type, ResultSet rs) throws SQLException
  {
    if (rs.getObject(type.name()) == null)
      return null;
    return rs.getLong(type.name());
  }

  public static Integer readInteger(FieldType type, ResultSet rs) throws SQLException
  {
    if (rs.getObject(type.name()) == null)
      return null;
    return rs.getInt(type.name());
  }

  public static Boolean readBoolean(FieldType type, ResultSet rs) throws SQLException
  {
    if (rs.getObject(type.name()) == null)
      return null;
    return rs.getBoolean(type.name());
  }

}

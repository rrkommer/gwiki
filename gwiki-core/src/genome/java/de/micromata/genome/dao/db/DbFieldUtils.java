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

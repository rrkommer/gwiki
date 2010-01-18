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

/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsDirectoryObject;
import de.micromata.genome.gdbfs.FsFileObject;
import de.micromata.genome.gdbfs.FsObject;


public class DbObjectRowMapper implements RowMapper
{
  protected DbFileSystemImpl dbFileSystem;

  protected boolean withData = false;

  public DbObjectRowMapper(DbFileSystemImpl dbFileSystem, boolean withData)
  {
    this.dbFileSystem = dbFileSystem;
    this.withData = withData;
  }

  public Object mapRow(ResultSet rs, int rowNum) throws SQLException
  {
    char type = rs.getString("TYPE").charAt(0);
    String mimeType = rs.getString("MIMETYPE");
    String name = rs.getString("NAME");
    long lastMod = 0;
    Date ld = rs.getDate("MODIFIEDAT");
    if (ld != null) {
      lastMod = ld.getTime();
    }
    FsObject ret = null;
    switch (type) {
      case FileSystem.TYPE_DIR:
        ret = new FsDirectoryObject(dbFileSystem, name, lastMod);
        break;
      case FileSystem.TYPE_FILE:
        if (withData == true) {
          ret = new FsDbFileObject(dbFileSystem, rs.getLong(dbFileSystem.getTable().getPkColumnName()), name, mimeType, lastMod, rs
              .getString("DATAENC"), rs.getString("DATACOL0"));
        } else {
          ret = new FsFileObject(dbFileSystem, name, mimeType, lastMod);
        }
        break;
      default:
        throw new RuntimeException("Unkown TYPE in filesystem. " + rs.getLong("base_gvfs"));
    }
    ret.setLength(rs.getInt("LENGTH"));
    ret.setAttributes(rs.getString("ATTRIBUTES"));
    ret.setCreatedAt(rs.getDate("CREATEDAT"));
    ret.setCreatedBy(rs.getString("CREATEDBY"));
    ret.setModifiedAt(rs.getDate("MODIFIEDAT"));
    ret.setModifiedBy(rs.getString("MODIFIEDBY"));
    ret.setUpdateCounter((int) rs.getLong("UPDATECOUNTER"));
    return ret;
  }
}

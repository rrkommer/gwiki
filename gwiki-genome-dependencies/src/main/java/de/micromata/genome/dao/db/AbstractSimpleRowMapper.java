/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    noodles@micromata.de
// Created   01.03.2008
// Copyright Micromata 01.03.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.dao.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

/**
 * Bietet ein paar Vereinfachungen zum Mappen von Objekten, Lesen von <code>null</code>-aware Integer-Werten etc.
 * 
 * Nicht threadsave, da ResultSet in Property gehalten wird.
 * 
 * @author noodles@micromata.de
 */
public abstract class AbstractSimpleRowMapper<T> implements RowMapper
{

  private ResultSet rs;

  private String prefix = "";

  public AbstractSimpleRowMapper()
  {
  }

  public AbstractSimpleRowMapper(String prefix)
  {
    this.prefix = StringUtils.defaultString(prefix);
  }

  public ResultSet getRs()
  {
    return rs;
  }

  public final Object mapRow(ResultSet rs, int rowNum) throws SQLException
  {
    this.rs = rs;
    final Object result = mapRow(rowNum);
    return result;
  }

  protected abstract T mapRow(int rowNum) throws SQLException;

  protected void mapStdRecordRows(StdRecordDO bean, FieldType pkField) throws SQLException
  {
    bean.setPk(readLong(pkField.name()));
    bean.setCreatedAt(readDate(StdRecordFieldTypes.CREATEDAT));
    bean.setCreatedBy(readString(StdRecordFieldTypes.CREATEDBY));
    bean.setModifiedAt(readDate(StdRecordFieldTypes.MODIFIEDAT));
    bean.setModifiedBy(readString(StdRecordFieldTypes.MODIFIEDBY));
    bean.setUpdateCounter(readInteger(StdRecordFieldTypes.UPDATECOUNTER));
  }

  protected BigDecimal readBigDecimal(String colname) throws SQLException
  {
    return this.rs.getBigDecimal(prefix + colname);
  }

  protected BigDecimal readBigDecimal(FieldType type) throws SQLException
  {
    return readBigDecimal(type.name());
  }

  protected String readString(String colname) throws SQLException
  {
    return rs.getString(prefix + colname);
  }

  protected String readString(FieldType type) throws SQLException
  {
    return readString(type.name());
  }

  protected Date readDate(String colname) throws SQLException
  {
    Timestamp ts = rs.getTimestamp(prefix + colname);
    if (ts == null) {
      return null;
    }
    return new Date(ts.getTime());
  }

  protected Date readDate(FieldType type) throws SQLException
  {
    return readDate(type.name());
  }

  protected Timestamp readTimestamp(String colname) throws SQLException
  {
    return rs.getTimestamp(prefix + colname);
  }

  protected Timestamp readTimestamp(FieldType type) throws SQLException
  {
    return readTimestamp(type.name());
  }

  protected Long readLong(String colname) throws SQLException
  {
    Long result = this.rs.getLong(prefix + colname);
    if (this.rs.wasNull() == true) {
      result = null;
    }
    return result;
  }

  protected Long readLong(FieldType type) throws SQLException
  {
    return readLong(type.name());
  }

  protected Integer readInteger(String colname) throws SQLException
  {
    Integer result = this.rs.getInt(prefix + colname);
    if (this.rs.wasNull() == true) {
      result = null;
    }
    return result;
  }

  protected Integer readInteger(FieldType type) throws SQLException
  {
    return readInteger(type.name());
  }

  protected Boolean readBoolean(String colname) throws SQLException
  {
    Boolean result = this.rs.getBoolean(prefix + colname);
    if (this.rs.wasNull() == true) {
      result = null;
    }
    return result;
  }

  protected Boolean readBoolean(FieldType type) throws SQLException
  {
    return readBoolean(type.name());
  }

  /**
   * Mappt den Wert <code>FALSE</code> auf <code>null</code>.
   */
  protected Boolean readTrueBoolean(FieldType type) throws SQLException
  {
    return readTrueBoolean(type.name());
  }

  /**
   * Mappt den Wert <code>FALSE</code> auf <code>null</code>.
   */
  protected Boolean readTrueBoolean(String colname) throws SQLException
  {
    Boolean result = this.rs.getBoolean(prefix + colname);
    if (this.rs.wasNull() == true || Boolean.FALSE.equals(result)) {
      result = null;
    }
    return result;
  }

}

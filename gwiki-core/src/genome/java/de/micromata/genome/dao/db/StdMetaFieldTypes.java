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

import static de.micromata.genome.dao.db.StdMetaFieldLength.LONGSYMBOL_LENGTH;
import static de.micromata.genome.dao.db.StdMetaFieldLength.PK_LENGTH;
import static de.micromata.genome.dao.db.StdMetaFieldLength.SHORTSYMBOL_LENGTH;
import static de.micromata.genome.dao.db.StdMetaFieldLength.SYMBOL_LENGTH;

import java.sql.Types;

public enum StdMetaFieldTypes
{
  VARCHAR(Types.VARCHAR), //
  NVARCHAR(Types.VARCHAR), //
  BOOLEAN(Types.BOOLEAN, 1), //
  DATE(Types.DATE), //
  PK(Types.BIGINT, PK_LENGTH), //
  COUNTER(Types.INTEGER, 9), //
  TIMESTAMP(Types.TIMESTAMP), //
  SHORTSYMBOL(Types.VARCHAR, SHORTSYMBOL_LENGTH), //
  SYMBOL(Types.VARCHAR, SYMBOL_LENGTH), //
  LONGSYMBOL(Types.VARCHAR, LONGSYMBOL_LENGTH), //
  ;

  private int sqlType;

  private int standardSize = -1;

  private boolean unicode = false;

  private StdMetaFieldTypes(int sqlType)
  {
    this.sqlType = sqlType;
  }

  private StdMetaFieldTypes(int sqlType, int standardSize)
  {
    this.sqlType = sqlType;
    this.standardSize = standardSize;
  }

  private StdMetaFieldTypes(int sqlType, int standardSize, boolean unicode)
  {
    this.sqlType = sqlType;
    this.standardSize = standardSize;
    this.unicode = unicode;
  }

  public int getSqlType()
  {
    return sqlType;
  }

  public int getStandardSize()
  {
    return standardSize;
  }

  public int getMaxSize()
  {
    return standardSize;
  }

  public boolean isUnicode()
  {
    return unicode;
  }
}

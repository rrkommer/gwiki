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

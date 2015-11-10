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
 * Just a wrapper to DatabaseProvider and its services
 * 
 * @author roger
 * 
 */
public class DatabaseProviderUtils
{
  final public static SpiDatabase dbSpi = new SpiDatabaseOracle();

  public static String getInPlaceSequenceSelect(String sequenceName)
  {
    return dbSpi.getInPlaceSequenceSelect(sequenceName);
  }

  public static String getSequenceSelect(String sequenceName)
  {
    return dbSpi.getSequenceSelect(sequenceName);
  }

  public static String getNowTimestamp()
  {
    return dbSpi.getNowTimestamp();
  }

}

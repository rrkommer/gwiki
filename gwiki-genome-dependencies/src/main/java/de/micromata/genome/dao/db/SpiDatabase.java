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
 * Interface to wrapp some DB operations
 * 
 * @author roger
 * 
 */
public interface SpiDatabase
{
  /**
   * Gives a inplace select expression for query next sequence value
   * 
   * @param sequenceName
   * @return
   */
  public String getInPlaceSequenceSelect(String sequenceName);

  /**
   * Get statement for query next sequnce value
   * 
   * @param sequenceName
   * @return
   */
  public String getSequenceSelect(String sequenceName);

  /**
   * Does this database supports uniq constraints with column, which may contains null
   * 
   * @return
   */
  public boolean supportUniqConstraintsWithNulls();

  /**
   * the sql litaral for now timestamp
   * 
   * @return
   */
  public String getNowTimestamp();

  /**
   * Creates a select statement with limit
   * 
   * @param limit limit count of results
   * @param selects all behind normal select, inclusive from
   * @param where where part. Can be null if no where part
   * @param order order part. Can be null if no where part
   * @return
   */
  public String createSelectWithLimit(String limit, String selects, String where, String order);
}

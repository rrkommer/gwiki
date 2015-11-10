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

// ///////////////////////////////////////////////////////////////////////////
//
// Project Genome Core
//
// Author roger@micromata.de
// Created 03.07.2006
// Copyright Micromata 03.07.2006
//
// ///////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.logging;

/**
 * has to be synchronized with db
 * 
 * @author roger
 */
public interface LogAttributeType
{
  LogAttributeType RootLogMessage = null;

  /**
   * @return enumeration name
   */
  public String name();

  /**
   * Will be ignored, if this LogAttributeType is not a searchKey.
   * 
   * @return db column name. Return null if no column name is given
   */
  public String columnName();

  /**
   * @return maximal column width. Will be ignored, if this LogAttributeType is not a searchKey.
   */
  public int maxValueSize();

  /**
   * @return true if this attribute can be searched
   */
  public boolean isSearchKey();

}
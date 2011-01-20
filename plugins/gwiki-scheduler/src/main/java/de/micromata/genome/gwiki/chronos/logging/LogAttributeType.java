// ///////////////////////////////////////////////////////////////////////////
//
// Project DHL-ParcelOnlinePostage
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
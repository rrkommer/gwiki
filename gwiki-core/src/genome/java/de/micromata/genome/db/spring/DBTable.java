/*
 Created on 08.01.2008
 */
package de.micromata.genome.db.spring;

/**
 * Simple interface for basic information for a database table
 * 
 * @author roger@micromata.de
 * 
 */
public interface DBTable
{
  public String getTableName();

  public String getPkColumnName();

  /**
   * @return may return null, if pk has no sequence
   */
  public String getPkSequenceName();
}

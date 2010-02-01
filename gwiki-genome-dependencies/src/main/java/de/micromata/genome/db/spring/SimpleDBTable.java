/*
 Created on 09.01.2008
 */
package de.micromata.genome.db.spring;

public class SimpleDBTable implements DBTable
{
  private String tableName;

  private String pkColumnName;

  private String pkSequenceName;

  public SimpleDBTable()
  {

  }

  public SimpleDBTable(String tableName)
  {
    this(tableName, null);
  }

  public SimpleDBTable(String tableName, String pkName)
  {
    this(tableName, pkName, null);
  }

  public SimpleDBTable(String tableName, String pkName, String pkSequenceName)
  {
    this.tableName = tableName;
    this.pkColumnName = pkName;
    this.pkSequenceName = pkSequenceName;
  }

  public String getPkColumnName()
  {
    return pkColumnName;
  }

  public String getTableName()
  {
    return tableName;
  }

  public void setPkColumnName(String pkColumnName)
  {
    this.pkColumnName = pkColumnName;
  }

  public void setTableName(String tableName)
  {
    this.tableName = tableName;
  }

  public String getPkSequenceName()
  {
    return pkSequenceName;
  }

  public void setPkSequenceName(String pkSequenceName)
  {
    this.pkSequenceName = pkSequenceName;
  }

}

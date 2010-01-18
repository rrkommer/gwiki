package de.micromata.genome.dao.db;

/**
 * SpiDatabase for HSQLDB
 * 
 * @author roger
 * 
 */
public class SpiDatabaseHSQLDB implements SpiDatabase
{

  public String getInPlaceSequenceSelect(String sequenceName)
  {
    return "(" + getSequenceSelect(sequenceName) + ")";
  }

  public String getNowTimestamp()
  {
    return " now ";
  }

  public String getSequenceSelect(String sequenceName)
  {
    return "select next value for " + sequenceName + " from dual_" + sequenceName;
  }

  public boolean supportUniqConstraintsWithNulls()
  {
    return false;
  }

  public String createSelectWithLimit(String limit, String selects, String where, String order)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("select TOP ").append(limit).append(" ").append(selects);
    if (where != null)
      sb.append(where);
    if (order != null)
      sb.append(order);
    return sb.toString();
  }

}

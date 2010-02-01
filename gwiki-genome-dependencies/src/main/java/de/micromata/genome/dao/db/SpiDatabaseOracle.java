package de.micromata.genome.dao.db;

/**
 * SpiDatabase for Oracle
 * 
 * @author roger
 * 
 */
public class SpiDatabaseOracle implements SpiDatabase
{
  /**
   * @see de.micromata.genome.dao.db.SpiDatabase.getInPlaceSequenceSelect(String)
   */
  public String getInPlaceSequenceSelect(String sequenceName)
  {
    return sequenceName + ".nextVal";
  }

  public String getNowTimestamp()
  {
    return " SYSTIMESTAMP ";
  }

  public String getSequenceSelect(String sequenceName)
  {
    return "select " + sequenceName + ".nextVal from DUAL";
  }

  public boolean supportUniqConstraintsWithNulls()
  {
    return true;
  }

  public String createSelectWithLimit(String limit, String selects, String where, String order)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("select ").append(selects);
    if (where != null) {
      sb.append(where);
      sb.append(" AND ROWNUM &lt;= ").append(limit);
    } else {
      sb.append(" WHERE ROWNUM &lt;= ").append(limit);
    }
    if (order != null)
      sb.append(order);
    return sb.toString();
  }
}

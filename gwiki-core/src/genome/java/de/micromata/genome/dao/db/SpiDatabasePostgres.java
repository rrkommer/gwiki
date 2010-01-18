package de.micromata.genome.dao.db;

/**
 * SpiDatabase for PostgreSQL
 * 
 * @author roger
 * 
 */
public class SpiDatabasePostgres implements SpiDatabase
{
  public String getInPlaceSequenceSelect(String sequenceName)
  {
    return "nextval('" + sequenceName + "')";
  }

  public String getNowTimestamp()
  {
    return " 'now' ";
  }

  public String getSequenceSelect(String sequenceName)
  {
    return "select nextval('" + sequenceName + "')";
  }

  public boolean supportUniqConstraintsWithNulls()
  {
    return false;
  }

  /**
   * select * from (select * from &CHRONOS_RESULT_TABLE; order by CREATEDAT) where TA_CHRONOS_JOB = #param# LIMIT 1000
   * 
   * @param limit
   * @param selects
   * @param where
   * @return
   */
  public String createSelectWithLimit(String limit, String selects, String where, String order)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("select ").append(selects);
    if (where != null)
      sb.append(where);
    if (order != null)
      sb.append(order);
    sb.append(" LIMIT ").append(limit);
    return sb.toString();
  }
}

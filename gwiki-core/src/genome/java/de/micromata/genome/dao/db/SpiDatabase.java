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
   * @param limit limit count of results
   * @param selects all behind normal select, inclusive from
   * @param where where part. Can be null if no where part
   * @param order order part. Can be null if no where part
   * @return 
   */
  public String createSelectWithLimit(String limit, String selects, String where, String order);
}

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

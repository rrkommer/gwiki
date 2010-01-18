package de.micromata.genome.dao.db;

/**
 * SpiDatabase for no database.
 * 
 * Every Method throws a UnsupportedOperationException
 * 
 * @author roger
 * 
 */
public class SpiDatabaseNONE implements SpiDatabase
{

  public String getInPlaceSequenceSelect(String sequenceName)
  {
    throw new UnsupportedOperationException("NONE Database does not support any DB-Operations");
  }

  public String getNowTimestamp()
  {
    throw new UnsupportedOperationException("NONE Database does not support any DB-Operations");
  }

  public String getSequenceSelect(String sequenceName)
  {
    throw new UnsupportedOperationException("NONE Database does not support any DB-Operations");
  }

  public boolean supportUniqConstraintsWithNulls()
  {
    throw new UnsupportedOperationException("NONE Database does not support any DB-Operations");
  }

  public String createSelectWithLimit(String limit, String selects, String where, String order)
  {
    throw new UnsupportedOperationException("NONE Database does not support any DB-Operations");
  }
}

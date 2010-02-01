package de.micromata.genome.dao.db;

public enum DatabaseProvider
{
  ORACLE(new SpiDatabaseOracle()), //
  POSTGRESQL(new SpiDatabasePostgres()), //
  HSQLDB(new SpiDatabaseHSQLDB()), //
  NONE(new SpiDatabaseNONE()), //
  ;
  private SpiDatabase spi;

  private DatabaseProvider(SpiDatabase spi)
  {
    this.spi = spi;
  }

  public SpiDatabase getSpi()
  {
    return spi;
  }

  public void setSpi(SpiDatabase spi)
  {
    this.spi = spi;
  }

}

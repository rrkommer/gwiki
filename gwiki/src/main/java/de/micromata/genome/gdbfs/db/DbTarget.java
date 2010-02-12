/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   12.01.2010
// Copyright Micromata 12.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs.db;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * Configuration of the database used by DbFileSystem.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class DbTarget
{
  /**
   * Has to be set from outside.
   */
  private DbDialect dbDialect;

  /**
   * Has to be set from outside.
   */

  private DataSource dataSource;

  /**
   * internal
   */
  private JdbcTemplate jtemplate;

  /**
   * internal
   */
  private DataSourceTransactionManager transactionManager;

  public DbTarget()
  {

  }

  public DbTarget(DbDialect dbDialect, DataSource dataSource)
  {
    this.dbDialect = dbDialect;
    this.dataSource = dataSource;
  }

  public JdbcTemplate getJtemplate()
  {
    if (jtemplate == null) {
      jtemplate = new JdbcTemplate();
      jtemplate.setDataSource(getDataSource());
    }
    return jtemplate;
  }

  public void setJtemplate(JdbcTemplate jtemplate)
  {
    this.jtemplate = jtemplate;
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }

  public void setDataSource(DataSource ds)
  {
    this.dataSource = ds;
  }

  public DataSourceTransactionManager getTransactionManager()
  {
    if (transactionManager == null) {
      transactionManager = new DataSourceTransactionManager(dataSource);
    }
    return transactionManager;
  }

  public TransactionTemplateX getTransactionTemplate(String name)
  {
    return getTransactionTemplate(TransactionPropagationEnum.SUPPORTS, name);
  }

  public TransactionTemplateX getTransactionTemplate(TransactionPropagation tranprop, String name)
  {
    return getTransactionTemplate(tranprop.getCode(), name);
  }

  public TransactionTemplateX getTransactionTemplate(int propagationBehavior, String name)
  {
    TransactionTemplateX tx = new TransactionTemplateX(getTransactionManager());
    tx.setPropagationBehavior(propagationBehavior);
    tx.setName(name);
    tx.afterPropertiesSet();
    return tx;
  }

  public void setTransactionManager(DataSourceTransactionManager transactionManager)
  {
    this.transactionManager = transactionManager;
  }

  public DbDialect getDbDialect()
  {
    return dbDialect;
  }

  public void setDbDialect(DbDialect dbDialect)
  {
    this.dbDialect = dbDialect;
  }

  public void setDbDialectName(String name)
  {
    this.dbDialect = DbDialectEnum.valueOf(name);
  }

}

////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

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

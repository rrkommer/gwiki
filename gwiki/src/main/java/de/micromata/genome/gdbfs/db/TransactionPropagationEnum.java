/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    jensi@micromata.de
// Created   27.11.2008
// Copyright Micromata 27.11.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs.db;

import org.springframework.transaction.TransactionDefinition;

public enum TransactionPropagationEnum implements TransactionPropagation
{
  /**
   * Support a current transaction, create a new one if none exists.
   * Analogous to EJB transaction attribute of the same name.
   * <p>This is typically the default setting of a transaction definition.
   */
  REQUIRED(TransactionDefinition.PROPAGATION_REQUIRED),

  /**
   * Support a current transaction, execute non-transactionally if none exists.
   * Analogous to EJB transaction attribute of the same name.
   * <p>Note: For transaction managers with transaction synchronization,
   * PROPAGATION_SUPPORTS is slightly different from no transaction at all,
   * as it defines a transaction scopp that synchronization will apply for.
   * As a consequence, the same resources (JDBC Connection, Hibernate Session, etc)
   * will be shared for the entire specified scope. Note that this depends on
   * the actual synchronization configuration of the transaction manager.
   * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization
   */
  SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS),

  /**
   * Support a current transaction, throw an exception if none exists.
   * Analogous to EJB transaction attribute of the same name.
   */
  MANDATORY(TransactionDefinition.PROPAGATION_MANDATORY),

  /**
   * Create a new transaction, suspend the current transaction if one exists.
   * Analogous to EJB transaction attribute of the same name.
   * <p>Note: Actual transaction suspension will not work on out-of-the-box
   * on all transaction managers. This in particular applies to JtaTransactionManager,
   * which requires the <code>javax.transaction.TransactionManager</code> to be
   * made available it to it (which is server-specific in standard J2EE).
   * see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
   */
  REQUIRES_NEW(TransactionDefinition.PROPAGATION_REQUIRES_NEW),

  /**
   * Execute non-transactionally, suspend the current transaction if one exists.
   * Analogous to EJB transaction attribute of the same name.
   * <p>Note: Actual transaction suspension will not work on out-of-the-box
   * on all transaction managers. This in particular applies to JtaTransactionManager,
   * which requires the <code>javax.transaction.TransactionManager</code> to be
   * made available it to it (which is server-specific in standard J2EE).
   * see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
   */
  NOT_SUPPORTED(TransactionDefinition.PROPAGATION_NOT_SUPPORTED),

  /**
   * Execute non-transactionally, throw an exception if a transaction exists.
   * Analogous to EJB transaction attribute of the same name.
   */
  NEVER(TransactionDefinition.PROPAGATION_NEVER),

  /**
   * Execute within a nested transaction if a current transaction exists,
   * behave like PROPAGATION_REQUIRED else. There is no analogous feature in EJB.
   * <p>Note: Actual creation of a nested transaction will only work on specific
   * transaction managers. Out of the box, this only applies to the JDBC
   * DataSourceTransactionManager when working on a JDBC 3.0 driver.
   * Some JTA providers might support nested transactions as well.
   * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
   */
  NESTED(TransactionDefinition.PROPAGATION_NESTED);


  private final int code;
  
  TransactionPropagationEnum(int code) {
    this.code = code;
  }
  
  public int getCode()
  {
    return code;
  }

}

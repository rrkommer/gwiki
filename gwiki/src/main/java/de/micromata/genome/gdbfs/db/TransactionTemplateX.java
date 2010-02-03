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

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionTemplateX extends TransactionTemplate
{

  private static final long serialVersionUID = 6496767223173079903L;

  protected TransactionTemplateX(PlatformTransactionManager transactionManager)
  {
    super(transactionManager);
  }

  public TransactionTemplateX()
  {
    super();
  }

  @SuppressWarnings("unchecked")
  public <O> O execute(TransactionCallbackX<O> action) throws TransactionException
  {
    return (O) super.execute(action);
  }

}

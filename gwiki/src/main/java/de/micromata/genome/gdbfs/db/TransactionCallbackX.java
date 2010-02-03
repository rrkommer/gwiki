/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    jensi@micromata.de
// Created   17.02.2009
// Copyright Micromata 17.02.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs.db;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public interface TransactionCallbackX<R> extends TransactionCallback
{
  R doInTransaction(TransactionStatus status);
}

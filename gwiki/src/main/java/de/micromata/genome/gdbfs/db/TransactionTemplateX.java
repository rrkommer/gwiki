////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gdbfs.jpa;

import javax.persistence.EntityManager;

import de.micromata.genome.jpa.DefaultEmgr;
import de.micromata.genome.jpa.EmgrFactory;
import de.micromata.genome.jpa.EmgrTx;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GwikiEmgrFactory extends EmgrFactory<DefaultEmgr>
{
  private static GwikiEmgrFactory INSTANCE;

  /**
   * Gets the.
   *
   * @return the jpa genome core ent mgr factory
   */
  public static synchronized GwikiEmgrFactory get()
  {
    if (INSTANCE != null) {
      return INSTANCE;
    }
    INSTANCE = new GwikiEmgrFactory();
    return INSTANCE;
  }

  public GwikiEmgrFactory()
  {
    super("de.micromata.genome.gdbfs.jpa");
  }

  @Override
  protected DefaultEmgr createEmgr(EntityManager entityManager, EmgrTx<DefaultEmgr> emgrTx)
  {
    return new DefaultEmgr(entityManager, this, emgrTx);
  }

}

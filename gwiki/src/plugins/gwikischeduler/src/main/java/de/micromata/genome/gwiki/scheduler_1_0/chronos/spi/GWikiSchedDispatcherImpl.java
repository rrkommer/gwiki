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
package de.micromata.genome.gwiki.scheduler_1_0.chronos.spi;

import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.spi.DispatcherImpl2;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSchedDispatcherImpl extends DispatcherImpl2
{

  /**
   * @param virtualHost
   * @param jobStore
   */
  public GWikiSchedDispatcherImpl(String virtualHost, JobStore jobStore)
  {
    super(virtualHost, jobStore);
  }

  public void run()
  {
    GWikiWeb.get().runInPluginContext(new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        GWikiSchedDispatcherImpl.super.run();
        return null;
      }
    });
  }
}

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

package de.micromata.genome.gwiki.scheduler_1_0.jobs;

import java.util.Map;

import de.micromata.genome.chronos.manager.AbstractMgcCommandLineJob;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Base class for GWiki plugin aware jobs.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiSchedBaseJob extends AbstractMgcCommandLineJob
{
  public abstract Object execute(Map<String, String> args) throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.chronos.manager.AbstractCommandLineJob#call(java.util.Map)
   */
  @Override
  public Object call(final Map<String, String> args) throws Exception
  {
    GWikiWeb wiki = GWikiWeb.get();
    return wiki.runInPluginContext(new CallableX<Object, Exception>()
    {

      @Override
      public Object call() throws Exception
      {
        return execute(args);
      }
    });
  }

}

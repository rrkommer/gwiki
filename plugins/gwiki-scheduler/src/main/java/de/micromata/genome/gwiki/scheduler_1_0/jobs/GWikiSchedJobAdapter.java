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
package de.micromata.genome.gwiki.scheduler_1_0.jobs;

import java.util.Map;

import de.micromata.genome.gwiki.chronos.manager.AbstractCommandLineJob;
import de.micromata.genome.gwiki.model.GWikiSchedulerJob;

/**
 * Adapter from scheduler job to gwiki internal job.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSchedJobAdapter extends AbstractCommandLineJob
{
  private GWikiSchedulerJob nested;

  public GWikiSchedJobAdapter(GWikiSchedulerJob nested)
  {
    this.nested = nested;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.chronos.manager.AbstractCommandLineJob#call(java.util.Map)
   */
  @Override
  public Object call(Map<String, String> args) throws Exception
  {
    return nested.call(args);
  }

}

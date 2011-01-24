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
package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.gwiki.chronos.manager.RAMSchedulerDAOImpl;
import de.micromata.genome.gwiki.chronos.manager.SchedulerDAO;
import de.micromata.genome.gwiki.chronos.manager.SchedulerManager;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class StaticDaoManager
{
  static StaticDaoManager INSTANCE = new StaticDaoManager();

  private SchedulerDAO schedulerDAO = new RAMSchedulerDAOImpl();

  private SchedulerManager schedulerManager = new SchedulerManager();

  public static StaticDaoManager get()
  {
    return INSTANCE;
  }

  public JobStore getJobStore()
  {
    return schedulerManager.getJobStore();
  }
  public SchedulerDAO getSchedulerDAO()
  {
    return schedulerDAO;
  }

  public void setSchedulerDAO(SchedulerDAO schedulerDAO)
  {
    this.schedulerDAO = schedulerDAO;
  }

  public SchedulerManager getSchedulerManager()
  {
    return schedulerManager;
  }

  public void setSchedulerManager(SchedulerManager schedulerManager)
  {
    this.schedulerManager = schedulerManager;
  }

}

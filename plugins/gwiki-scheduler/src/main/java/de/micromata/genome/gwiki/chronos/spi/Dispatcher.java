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

package de.micromata.genome.gwiki.chronos.spi;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.SchedulerConfigurationException;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;

/**
 * Interface which implements the Thread, which dispatches Scheduler Requests
 * 
 * @author roger
 * 
 */
public interface Dispatcher
{
  /**
   * return true, if the dispatcher is running.
   * 
   * @return
   */
  public boolean isRunning();

  /**
   * Starts the Dispatcher
   */
  public void startup();

  /**
   * @see de.micromata.genome.chronos.spi.Dispatcher.shutdown(long)
   * @throws InterruptedException
   */
  public void shutdown() throws InterruptedException;

  /**
   * 
   * @param waitForShutdown time to wait for shutdown. <= 0 means wait forever.
   * @throws InterruptedException
   */
  public void shutdown(final long waitForShutdown) throws InterruptedException;

  /**
   * Set the minimal node bind timeout
   * 
   * @param minNodeBindTimeout
   */
  public void setMinNodeBindTime(long minNodeBindTimeout);

  /**
   * Gibt den Scheduler mit dem angegebenen Namen zurück oder <code>null</code>.
   * <p>
   * Hier wird <u>nicht</u> auf die Datenbank zugegriffen. Dafür ist {@link #createOrGetScheduler(SchedulerDO)} zu benutzen.
   * </p>
   * 
   * @param name
   * @return
   */
  public Scheduler getScheduler(final String name);

  /**
   * Gibt zu einem {@link SchedulerDO} die entspechende reinitialisierte {@link Scheduler} zurück, oder erzeugt diese neu.
   * <p>
   * Ein neu angelegter Scheduler wird unmittelbar persisitiert und unter dem Namen abgespeichert.
   * </p>
   * 
   * @param schedulerDO
   * @return
   * @see #schedulers
   */
  public Scheduler createOrGetScheduler(final SchedulerDO schedulerDO);

  /**
   * Submits a new Job.
   * 
   * If the dispatcher is not started, the job will just inserted into the JobStore.
   * 
   * @param schedulerName must not be null
   * @param jobDefinition must not be null
   * @param arg may be null
   * @param trigger must not be null
   * @param hostName if null, uses the current hostname
   * @return Job reference (pk)
   * @throws SchedulerConfigurationException wenn ein nicht registrierter Scheduler angesprochen wird
   */
  public long submit(String schedulerName, JobDefinition jobDefinition, Object arg, Trigger trigger, String hostName);

  /**
   * Submits a new Job.
   * 
   * If the dispatcher is not started, the job will just inserted into the JobStore.
   * 
   * @param schedulerName must not be null
   * @param jobDefinition must not be null
   * @param arg may be null
   * @param trigger must not be null
   * @param hostName if null, uses the current hostname
   * @return Job reference (pk)
   * @throws SchedulerConfigurationException wenn ein nicht registrierter Scheduler angesprochen wird
   */
  public long submit(String schedulerName, String jobName, JobDefinition jobDefinition, Object arg, Trigger trigger, String hostName);

  /**
   * Persists the given schedulder
   * 
   * @param scheduler
   */
  public void persist(SchedulerDO scheduler);

  /**
   * Es werden keinen neuen Jobs mehr gestartet. ThreadCount will be set to 0.
   * 
   * @param name
   */
  public void denyNewJobs(final String schedulerName);

  /**
   * set the number of paralell threads jobs should be executed on given scheduler.
   * 
   * @param size >= 0. 0 means no more jobs should be executen on this scheduler
   * @param schedulerName
   */
  public void setJobCount(final int size, final String schedulerName);

  /**
   * returns the JobStore assigned with this dispatcher
   * 
   * @return
   */
  public JobStore getJobStore();

  /**
   * 
   * @return the (virtual or real) host name this dispatcher is running
   */
  public String getVirtualHostName();
}

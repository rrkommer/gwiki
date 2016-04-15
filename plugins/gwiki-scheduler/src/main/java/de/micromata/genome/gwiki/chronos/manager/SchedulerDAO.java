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

/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   24.01.2008
// Copyright Micromata 24.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.manager;

import de.micromata.genome.gwiki.chronos.spi.Dispatcher;
import de.micromata.genome.gwiki.chronos.spi.JobRunner;

/**
 * Interface for the Chronos Scheduler
 * 
 * @author roger@micromata.de
 * 
 */
public interface SchedulerDAO
{

  /**
   * Create a Dispatcher instance.
   *
   * @param virtualHostName the virtual host name
   * @return the dispatcher
   * @throws Exception if any error occurs
   */
  public Dispatcher createDispatcher(String virtualHostName) throws Exception;

  /**
   * Apply Filter chain to Job run
   * 
   * @param jobRunner
   */
  public Object filterJobRun(JobRunner jobRunner) throws Exception;

  /**
   * Invoke Job dispatched from Dispatcher
   * 
   * @param jobRunner
   * @return
   * @throws Exception
   */
  public Object invokeJob(JobRunner jobRunner) throws Exception;

}

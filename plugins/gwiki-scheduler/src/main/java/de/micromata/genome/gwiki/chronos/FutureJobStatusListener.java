////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import de.micromata.genome.gwiki.chronos.spi.JobRunner;
import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;

/**
 * If a FutureJob implements this interface the methods will be called by scheduler
 * 
 * @author roger
 * 
 */
public interface FutureJobStatusListener
{
  /**
   * This method will be called if a Job will stop and no retry will be tried.
   * 
   * This call back will be called after the job is already stopped on the jobstore
   * 
   * @param jobRunner get Access to job, jobStore and scheduler
   */
  public void finalFail(JobRunner jobRunner, final JobResultDO resultInfo, final Exception ex);
}

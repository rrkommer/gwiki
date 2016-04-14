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
// Created   29.03.2008
// Copyright Micromata 29.03.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.manager;

import java.util.List;

import de.micromata.genome.gwiki.chronos.FutureJob;
import de.micromata.genome.gwiki.chronos.spi.JobRunner;

public abstract class SchedulerBaseDAO implements SchedulerDAO
{

  private JobFilterChain createFilterChain(JobRunner jobRunner)
  {

    JobFilterChain fc = new JobFilterChain();
    fc.setJobRunner(jobRunner);
    fc.setSchedulerDAO(this);
    SchedulerManager sm = SchedulerManager.get();
    if (sm != null) {
      List<JobRunnerFilter> filters = sm.getFilters(jobRunner.getScheduler().getName());
      fc.setFilters(filters);
    }
    return fc;
  }

  public Object filterJobRun(JobRunner jobRunner) throws Exception
  {

    JobFilterChain chain = createFilterChain(jobRunner);
    return chain.doFilter();
  }

  public Object invokeJob(final JobRunner jobRunner) throws Exception
  {

    final String jobName = jobRunner.getJob().getClass().getSimpleName();
    // Folgende Zeile baut ein neues FutureJob-Objekt
    FutureJob fjob = jobRunner.getJob().getExecutor();
    jobRunner.setFutureJob(fjob);
    final Object result = fjob.call(jobRunner.getJob().getJobArguments());
    return result;
  }

}

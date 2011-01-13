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

import de.micromata.genome.gwiki.model.GWikiPropKeys;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiSchedPropKeys extends GWikiPropKeys
{
  public static final String SCHED_JOB_CLASS = "SCHED_JOB_CLASS";

  public static final String SCHED_JOB_NAME = "SCHED_JOB_NAME";

  public static final String SCHED_JOB_SCHEDULER = "SCHED_JOB_SCHEDULER";

  public static final String SCHED_JOB_NODE = "SCHED_JOB_NODE";

  public static final String SCHED_JOB_TRIGGER = "SCHED_JOB_TRIGGER";

  public static final String SCHED_JOB_ARGS = "SCHED_JOB_ARGS";

  public static final String SCHED_JOB_STATE = "SCHED_JOB_STATE";

}

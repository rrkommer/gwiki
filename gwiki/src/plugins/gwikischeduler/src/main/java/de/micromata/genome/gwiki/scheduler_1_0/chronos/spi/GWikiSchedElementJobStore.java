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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SerializationUtil;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.gwiki.chronos.spi.ram.RamJobStore;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.PopulateMacroBeansMacroVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;
import de.micromata.genome.gwiki.scheduler_1_0.macros.GWikiSchedJobDefineMacroBean;
import de.micromata.genome.gwiki.scheduler_1_0.macros.GWikiSchedSchedDefineMacroBean;
import de.micromata.genome.util.web.HostUtils;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSchedElementJobStore extends RamJobStore
{
  private static final long RELOAD_PERIOD = 30000;

  private long lastLoaded = 0;

  @Override
  public List<TriggerJobDO> getNextJobs(final Scheduler scheduler, final boolean foreignJobs)
  {
    loadStandardJobs();
    return GWikiSchedElementJobStore.super.getNextJobs(scheduler, foreignJobs);

  }

  public List<TriggerJobDO> getNextJobs(final long lookForward)
  {
    loadStandardJobs();
    return GWikiSchedElementJobStore.super.getNextJobs(lookForward);

  }

  private boolean needReload()
  {
    long ct = System.currentTimeMillis();
    if (lastLoaded + RELOAD_PERIOD < ct) {
      lastLoaded = ct;
      return true;
    }
    return false;
  }

  private long getExistantJobPk(String schedName, String jobName)
  {
    Map<Long, TriggerJobDO> sm = allJobs.get(schedName);
    if (sm == null) {
      return -1;
    }
    for (Map.Entry<Long, TriggerJobDO> me : sm.entrySet()) {
      if (jobName.equals(me.getValue().getJobName()) == true) {
        return me.getKey();
      }
    }
    return -1;
  }

  public void loadStandardJobs()
  {
    if (needReload() == false) {
      return;
    }
    Map<String, SchedulerDO> nschedulers = new TreeMap<String, SchedulerDO>();
    Map<String, Map<String, TriggerJobDO>> nallJobs = new TreeMap<String, Map<String, TriggerJobDO>>();
    GWikiElement el = GWikiWeb.get().findElement("admin/system/scheduler/StandardJobs");
    if (el != null) {
      loadJobs(el, nschedulers, nallJobs);
    }

    for (Map.Entry<String, SchedulerDO> p : nschedulers.entrySet()) {
      this.schedulers.put(p.getKey(), p.getValue());
    }
    for (Map.Entry<String, Map<String, TriggerJobDO>> jm : nallJobs.entrySet()) {
      Map<Long, TriggerJobDO> jt = allJobs.get(jm.getKey());
      if (jt == null) {
        jt = new HashMap<Long, TriggerJobDO>();
        allJobs.put(jm.getKey(), jt);
      }
      for (Map.Entry<String, TriggerJobDO> me : jm.getValue().entrySet()) {
        long pk = getExistantJobPk(jm.getKey(), me.getKey());
        if (pk == -1) {
          pk = getNextJobId();
        }
        me.getValue().setPk(pk);
        jt.put(pk, me.getValue());
      }

    }
  }

  SchedulerDO createSchedulerDO(GWikiSchedSchedDefineMacroBean sched)
  {
    SchedulerDO ret = new SchedulerDO();
    ret.setName(sched.getName());
    return ret;
  }

  TriggerJobDO createTriggerJobDO(String currentScheduler, GWikiSchedJobDefineMacroBean bean, MacroAttributes attrs)
  {
    TriggerJobDO job = new TriggerJobDO();
    job.setSchedulerName(currentScheduler);
    job.setJobName(bean.getName());
    job.setJobDefinitionString(bean.getClassName());
    job.setTriggerDefinition(bean.getTrigger());
    job.setArgumentDefinitionString(SerializationUtil.serializeJobArguments(attrs.getArgs().getMap()));
    State st = State.fromString(bean.getState());
    if (st == null) {
      st = State.WAIT;
    }
    job.setState(st);
    job.setHostName(HostUtils.getNodeName());
    job.setNextFireTime(job.getTrigger().getNextFireTime(new Date()));
    return job;
  }

  /**
   * TODO load first, if new job, etc.
   * 
   * @param el
   * @param schedulers
   * @param allJobs
   */
  public void loadJobs(GWikiElement el, final Map<String, SchedulerDO> schedulers, final Map<String, Map<String, TriggerJobDO>> allJobs)
  {
    if (el == null) {
      return;
    }
    GWikiArtefakt< ? > art = el.getPart("MainPage");
    if ((art instanceof GWikiWikiPageArtefakt) == false) {
      GWikiLog.warn("Scheduler; MainPart is not a wiki: " + el.getElementInfo().getId());
      return;
    }

    GWikiWikiPageArtefakt page = (GWikiWikiPageArtefakt) art;
    GWikiContent cont = page.getCompiledObject();
    GWikiContext wikiContext = GWikiStandaloneContext.create();
    wikiContext.setRenderMode(RenderModes.combine(RenderModes.InMem));
    if (cont == null) {

      page.compileFragements(wikiContext);
      cont = page.getCompiledObject();
    }
    if (cont == null) {
      return;
    }
    cont.iterate(new PopulateMacroBeansMacroVisitor(wikiContext));
    GWikiSimpleFragmentVisitor vis = new GWikiSimpleFragmentVisitor() {
      String currentScheduler;

      public void begin(GWikiFragment fragment)
      {
        if ((fragment instanceof GWikiMacroFragment) == false) {
          return;
        }
        GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
        if (mf.getMacro() instanceof GWikiSchedSchedDefineMacroBean) {
          GWikiSchedSchedDefineMacroBean mdm = (GWikiSchedSchedDefineMacroBean) mf.getMacro();
          SchedulerDO sched = createSchedulerDO(mdm);
          schedulers.put(sched.getName(), sched);
          currentScheduler = sched.getName();
        } else if (mf.getMacro() instanceof GWikiSchedJobDefineMacroBean) {
          if (StringUtils.isEmpty(currentScheduler) == true) {
            GWikiLog.warn("Scheduler: No job definition macro must be enclosed by scheduler");
            return;
          }
          GWikiSchedJobDefineMacroBean jdm = (GWikiSchedJobDefineMacroBean) mf.getMacro();
          TriggerJobDO job = createTriggerJobDO(currentScheduler, jdm, mf.getAttrs());
          Map<String, TriggerJobDO> jm = allJobs.get(currentScheduler);
          if (jm == null) {
            jm = new TreeMap<String, TriggerJobDO>();
            allJobs.put(currentScheduler, jm);
          }
          jm.put(job.getJobName(), job);
        }
      }

      public void end(GWikiFragment fragment)
      {
        if ((fragment instanceof GWikiMacroFragment) == false) {
          return;
        }
        GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
        if (mf.getMacro() instanceof GWikiSchedSchedDefineMacroBean) {
          currentScheduler = null;
        }
      }
    };
    cont.iterate(vis);
  }
}

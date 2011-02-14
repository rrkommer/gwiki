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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.StaticDaoManager;
import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SerializationUtil;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.gwiki.chronos.spi.ram.RamJobStore;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.matcher.GWikiElementPropMatcher;
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
import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.web.HostUtils;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSchedElementJobStore extends RamJobStore
{
  private static final long RELOAD_PERIOD = 30000;

  private long lastLoaded = 0;

  private static final String DYNAMIC_JOBS_PARENT = "admin/system/scheduler/GWikiSchedulerJobs";

  private boolean firstLoaded = false;

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

  private void createStandardScheduler()
  {
    StaticDaoManager.get().getSchedulerManager().getScheduler("standard");
  }

  public void loadStandardJobs()
  {
    if (needReload() == false) {
      return;
    }
    if (firstLoaded == false) {
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
      firstLoaded = true;
    }
    List<TriggerJobDO> djl = getDynamicJobs();
    for (TriggerJobDO job : djl) {
      String schedName = job.getSchedulerName();
      if (StringUtils.isBlank(schedName) == true) {
        schedName = "standard";

      }
      Map<Long, TriggerJobDO> jt = allJobs.get(schedName);
      if (jt == null) {
        jt = new HashMap<Long, TriggerJobDO>();
        allJobs.put(schedName, jt);
      }
      // only put into alljobs, if not already exists. otherwise every time firetime will be overwritten.
      if (jt.containsKey(job.getPk()) == false) {
        jt.put(job.getPk(), job);
      }
    }
    createStandardScheduler();
  }

  private List<TriggerJobDO> getDynamicJobs()
  {
    List<TriggerJobDO> ret = new ArrayList<TriggerJobDO>();
    GWikiContext wikiContext = GWikiContext.getCreateContext();
    GWikiElementInfo pei = wikiContext.getWikiWeb().findElementInfo(DYNAMIC_JOBS_PARENT);
    if (pei == null) {
      return ret;
    }

    List<GWikiElementInfo> childs = wikiContext.getElementFinder().getPageInfos(//
        new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, //
            new EqualsMatcher<String>(pei.getId()))//
        );

    for (GWikiElementInfo ei : childs) {
      TriggerJobDO trigger = createJobByPage(ei);
      ret.add(trigger);
    }
    return ret;
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

  private String getPageIdByJobId(long pk)
  {
    return "admin/system/scheduler/job_" + pk;
  }

  private long getJobIdByPageId(String pageId)
  {
    int idx = pageId.lastIndexOf('_');
    if (idx != -1) {
      return Long.valueOf(pageId.substring(idx + 1));
    }
    return -1;
  }

  private long getNextJobId(GWikiContext wikiContext)
  {
    for (int i = 0; i < 100000; ++i) {
      String jobId = getPageIdByJobId(i);
      if (wikiContext.getWikiWeb().findElement(jobId) == null) {
        return i;
      }
    }
    throw new RuntimeException("Scheduler; cannot find free job pk");
  }

  private GWikiElement createJobElement(GWikiContext wikiContext, TriggerJobDO job)
  {

    String metaTemplateId = "admin/templates/intern/SchedJobMetaTemplate";
    long id = getNextJobId(wikiContext);
    GWikiElement jobel = GWikiWebUtils.createNewElement(wikiContext, "admin/system/scheduler/job_" + id, metaTemplateId, "Job " + id);
    job.setPk(id);
    mapJobToPageInfo(job, jobel.getElementInfo());
    return jobel;
  }

  private void mapJobToPageInfo(TriggerJobDO job, GWikiElementInfo ei)
  {
    final GWikiProps props = ei.getProps();
    JobDefinition jd = job.getJobDefinition();
    if (jd instanceof GWikiSchedClassJobDefinition) {
      props.setStringValue(GWikiSchedPropKeys.SCHED_JOB_CLASS, ((GWikiSchedClassJobDefinition) jd).serialize());
    } else {
      throw new RuntimeException("Cannot support JobDefintion type: " + job.getClass().getName() + "; " + job);
    }
    props.setStringValue(GWikiSchedPropKeys.SCHED_JOB_SCHEDULER, job.getSchedulerName());
    props.setStringValue(GWikiSchedPropKeys.SCHED_JOB_TRIGGER, job.getTriggerDefinition());
    props.setStringValue(GWikiSchedPropKeys.SCHED_JOB_STATE, State.WAIT.name());
    props.setStringValue(GWikiSchedPropKeys.SCHED_JOB_ARGS, job.getArgumentDefinitionString());
    String jobName = job.getJobName();
    if (StringUtils.isNotBlank(jobName) == true) {
      props.setStringValue(GWikiSchedPropKeys.SCHED_JOB_NAME, jobName);
    }
    props.setStringValue(GWikiSchedPropKeys.SCHED_JOB_ARGS, job.getArgumentDefinitionString());
    props.setStringValue(GWikiSchedPropKeys.PARENTPAGE, DYNAMIC_JOBS_PARENT);
  }

  private TriggerJobDO createJobByPage(GWikiElementInfo ei)
  {
    TriggerJobDO job = new TriggerJobDO();
    job.setPk(getJobIdByPageId(ei.getId()));
    final GWikiProps props = ei.getProps();

    job.setSchedulerName(props.getStringValue(GWikiSchedPropKeys.SCHED_JOB_SCHEDULER));
    job.setCreatedAt(props.getDateValue(GWikiSchedPropKeys.CREATEDAT));
    job.setModifiedAt(props.getDateValue(GWikiSchedPropKeys.MODIFIEDAT));
    job.setJobName(props.getStringValue(GWikiSchedPropKeys.SCHED_JOB_NAME));
    job.setJobDefinitionString(props.getStringValue(GWikiSchedPropKeys.SCHED_JOB_CLASS));
    job.setTriggerDefinition(props.getStringValue(GWikiSchedPropKeys.SCHED_JOB_TRIGGER));
    job.setArgumentDefinitionString(props.getStringValue(GWikiSchedPropKeys.SCHED_JOB_ARGS));
    State st = State.fromString(props.getStringValue(GWikiSchedPropKeys.SCHED_JOB_STATE));
    if (st == null) {
      st = State.WAIT;
    }
    job.setState(st);
    job.setHostName(HostUtils.getNodeName()); // todo
    job.setNextFireTime(job.getTrigger().getNextFireTime(new Date()));
    return job;
  }

  @Override
  public void insertJob(TriggerJobDO job)
  {
    GWikiContext wikiContext = GWikiContext.getCreateContext();
    GWikiElement el = createJobElement(wikiContext, job);
    GWikiLog.note("Scheduler; insertJob: " + job);
    super.insertJob(job);
    wikiContext.getWikiWeb().getStorage().storeElement(wikiContext, el, false);
  }

  @Override
  public void jobRemove(final TriggerJobDO job, final JobResultDO jobResult, final Scheduler scheduler)
  {

    final GWikiContext wikiContext = GWikiContext.getCreateContext();
    wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        GWikiLog.note("Scheduler; jobRemove: " + job);
        GWikiSchedElementJobStore.super.jobRemove(job, jobResult, scheduler);

        String pageId = getPageIdByJobId(job.getId());
        GWikiElement el = wikiContext.getWikiWeb().findElement(pageId);
        if (el != null) {
          wikiContext.getWikiWeb().getStorage().deleteElement(wikiContext, el);
        }
        return null;
      }
    });

  }

  @Override
  public void updateJob(final TriggerJobDO job)
  {
    final GWikiContext wikiContext = GWikiContext.getCreateContext();
    wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        GWikiLog.note("Scheduler; updateJob: " + job);

        GWikiSchedElementJobStore.super.updateJob(job);
        String pageId = getPageIdByJobId(job.getId());
        GWikiElement el = wikiContext.getWikiWeb().findElement(pageId);
        if (el != null) {
          mapJobToPageInfo(job, el.getElementInfo());
          wikiContext.getWikiWeb().getStorage().storeElement(wikiContext, el, false);
        }
        return null;
      }
    });

  }

  @Override
  public void insertResult(JobResultDO result)
  {
    GWikiLog.note("Scheduler; insertResult: " + result);
    super.insertResult(result);
  }

  @Override
  public void jobResultRemove(TriggerJobDO job, JobResultDO jobResult, Scheduler scheduler)
  {
    GWikiLog.note("Scheduler; jobResultRemove: " + job);
    super.jobResultRemove(job, jobResult, scheduler);
  }

  @Override
  public void persist(SchedulerDO scheduler)
  {
    GWikiLog.note("Scheduler; persist Scheduler: " + scheduler);
    super.persist(scheduler);
  }

  @Override
  public TriggerJobDO reserveJob(TriggerJobDO job)
  {
    GWikiLog.note("Scheduler; reserveJob: " + job);
    return super.reserveJob(job);
  }

  @Override
  public int setJobState(long pk, String newState, String oldState)
  {
    GWikiLog.note("Scheduler; setJobState: " + pk);
    if (super.setJobState(pk, newState, oldState) == 1) {
      if (StringUtils.equals(newState, State.CLOSED.name()) == true) {
        TriggerJobDO job = getAdminJobByPk(pk);
        jobRemove(job, null, getDispatcher().getScheduler(job.getSchedulerName()));
      } else {
        updateJob(getAdminJobByPk(pk));
      }
      return 1;
    }
    return 0;
  }
}

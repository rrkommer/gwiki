/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: JobRunner.java,v $
//
// Project   genome
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   03.01.2007
// Copyright Micromata 03.01.2007
//
// $Id: JobRunner.java,v 1.12 2007/03/14 10:46:45 roger Exp $
// $Revision: 1.12 $
// $Date: 2007/03/14 10:46:45 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.util.Date;

import de.micromata.genome.gwiki.chronos.ForceRetryException;
import de.micromata.genome.gwiki.chronos.FutureJob;
import de.micromata.genome.gwiki.chronos.FutureJobStatusListener;
import de.micromata.genome.gwiki.chronos.JobAbortException;
import de.micromata.genome.gwiki.chronos.JobCompletion;
import de.micromata.genome.gwiki.chronos.JobRetryException;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.RetryNextRunException;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.ServiceUnavailableException;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.StaticDaoManager;
import de.micromata.genome.gwiki.chronos.logging.GLog;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;
import de.micromata.genome.gwiki.chronos.manager.LogJobEventAttribute;
import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.util.web.HostUtils;

/**
 * Ausführungsobjekt, welches das wirkliche Runtime-Job-Objekt erzeugt, startet und die Fehlerbehandlung tätigt.
 * 
 * 
 */
public class JobRunner implements Runnable
{

  private Scheduler scheduler;

  private TriggerJobDO job;

  private JobStore jobStore;

  private FutureJob futureJob;

  public JobRunner(final Scheduler scheduler, final TriggerJobDO job)
  {
    this.scheduler = scheduler;
    this.job = job;

    this.jobStore = scheduler.getDispatcher().getJobStore();

    if (GLog.isTraceEnabled() == true)
      GLog.trace(GenomeLogCategory.Scheduler, "New JobRunner", new LogJobEventAttribute(new JobEventImpl(job, job.getJobDefinition(), null,
          null, scheduler)));
  }

  // /**
  // * Falls es eine LogAttributeException vorhanden ist, nimm es. Sonst die originalle Exception
  // *
  // * @param ex
  // * @param attributes
  // * @return
  // */
  // protected LogAttribute getLogExceptionAttribute(LogAttributeRuntimeException ex)
  // {
  // if (ex.getLogAttributeMap().containsKey(GenomeAttributeType.TechReasonException.name())) {
  // return ex.getLogAttributeMap().get(GenomeAttributeType.TechReasonException.name());
  // }
  // return new LogExceptionAttribute(ex);
  // }

  /**
   * Hier wird der Runtime-Job aus dem {@link TriggerJobDO} erzeugt und gestartet.
   * 
   * @see java.lang.Runnable#run()
   */
  @SuppressWarnings("unused")
  public void run()
  {
    // TODO genome hier filter concept umsetzen
    String state = "start";
    Exception handleEx = null;
    JobResultDO resultInfo = null;
    JobEventImpl event = null;
    // LoggingContext.createNewContext();
    // ScopedLogContextAttribute threadContextScope = new ScopedLogContextAttribute(GenomeAttributeType.ThreadContext, HostUtils
    // .getRunContext());
    try {
      SchedulerThread sct = (SchedulerThread) Thread.currentThread();
      sct.setJobId(job.getPk());
      // ScopedLogContextAttribute jobIdScope = new ScopedLogContextAttribute(GenomeAttributeType.GenomeJobId, ObjectUtils.toString(job
      // .getPk()));
      try {

        job.setHostName(HostUtils.getNodeName());
        if (GLog.isTraceEnabled() == true)
          GLog.trace(GenomeLogCategory.Scheduler, "Start job", new LogJobEventAttribute(new JobEventImpl(job, job.getJobDefinition(), null,
              null, scheduler)));

        resultInfo = jobStore.jobStarted(job, scheduler);
        event = new JobEventImpl(job, job.getJobDefinition(), resultInfo, null, scheduler);
        if (GLog.isInfoEnabled() == true)
          GLog.info(GenomeLogCategory.Scheduler, "JobRunner resultInfo: " + resultInfo, new LogJobEventAttribute(event));
        final Object result = StaticDaoManager.get().getSchedulerDAO().filterJobRun(this);
        // final Object result =
        // job.getExecutor().call(job.getJobArguments());
        state = "completed";
        completedJob(resultInfo, result);
      } catch (final ServiceUnavailableException ex) {
        state = "ServiceUnavailableException";
        handleEx = ex;
        if (ex.isSilent() == false) {
          /**
           * @logging
           * @reasin
           * @action
           */
          GLog.warn(GenomeLogCategory.Scheduler, "JobRunner serviceUnavailabe: " + ex, ex);// new LogJobEventAttribute(event));
        }
        handleServiceError(resultInfo, ex);
      } catch (final ForceRetryException ex) {
        state = "ForceRetryException";
        handleEx = ex;
        if (ex.isSilent() == false) {
          /**
           * @logging
           * @reasin
           * @action
           */
          GLog.warn(GenomeLogCategory.Scheduler, "JobRunner force retry: " + ex, ex);// , new LogJobEventAttribute(event));
        }
        handleRetry(resultInfo, ex);
      } catch (final RetryNextRunException ex) {
        state = "RetryNextRunException";
        handleEx = ex;
        if (ex.isSilent() == false) {
          GLog.warn(GenomeLogCategory.Scheduler, "JobRunner abort with nextretry: " + ex, ex);
          // new LogJobEventAttribute(event));
        }
        handleRetryNextRun(resultInfo, ex);
      } catch (final JobRetryException ex) {
        handleEx = ex;
        state = "RetryException";
        if (ex.isSilent() == false) {
          GLog.warn(GenomeLogCategory.Scheduler, "JobRunner retry: " + ex, ex);
          // , new LogJobEventAttribute(event));
        }
        handleUnexpected(resultInfo, ex);
      } catch (final JobAbortException ex) {
        handleEx = ex;
        state = "JobAbortException";

        /**
         *@logging
         *@reason
         *@action
         */
        GLog.warn(GenomeLogCategory.Scheduler, "JobRunner abort: " + ex, ex);// , new LogJobEventAttribute(event));
        handleFailure(resultInfo, ex);
      } catch (final Exception ex) {
        handleEx = ex;
        state = "Exception";
        /**
         * @logging
         * @reason
         * @action
         * 
         */
        GLog.warn(GenomeLogCategory.Scheduler, "JobRunner unexpected: " + ex, ex);
        // new LogJobEventAttribute(event));
        handleUnexpected(resultInfo, ex);
      } finally {

        sct.setJobId(-1);
        // if (jobIdScope != null)
        // jobIdScope.restore();
        scheduler.getDispatcher().wakeup(); // problem this synchronizes Dispatcher
      }
    } catch (Exception ex) {
      try {
        long jobId = -1;
        if (job != null)
          jobId = job.getPk();
        /**
         * @logging
         * @reason
         * @action
         */
        GLog.error(GenomeLogCategory.Scheduler, "JobRunner fail handle: " + state + "; jobId: " + jobId + "; " + ex.getMessage(), ex);
      } catch (Exception ex1) {
        // OOOPs
      }
    } finally {
      // if (threadContextScope != null)
      // threadContextScope.restore();
      // DynDaoManager.threadDeInitDynDaoManager();
    }
  }

  public long getJobId()
  {
    if (job == null)
      return -1;
    return job.getPk();
  }

  private void handleUnexpected(final JobResultDO resultInfo, final Exception ex)
  {
    if (scheduler.getJobMaxRetryCount() <= job.getRetryCount()) {
      handleFailure(resultInfo, ex);
    } else {
      handleRetry(resultInfo, ex);
    }
  }

  /**
   * @param resultInfo
   * @param ex
   */
  private void handleFailure(final JobResultDO resultInfo, final Exception ex)
  {
    if (GLog.isTraceEnabled() == true) {
      /**
       * @logging
       * @reason
       *@action
       */
      GLog.trace(GenomeLogCategory.Scheduler, "Failed job", new LogJobEventAttribute(new JobEventImpl(job, job.getJobDefinition(), null,
          State.STOP, scheduler)));
    }
    Date nextRun = job.getTrigger().updateAfterRun(scheduler, JobCompletion.EXCEPTION);
    job.setFireTime(nextRun);
    if (nextRun != null) {
      job.setState(State.WAIT);
      // TODO signal Dispatcher, that wait should be aborted
    } else {
      job.setState(State.STOP);
    }
    jobStore.jobAborted(job, resultInfo, ex, scheduler);
    if (getFutureJob() instanceof FutureJobStatusListener) {
      FutureJobStatusListener listener = (FutureJobStatusListener) getFutureJob();
      listener.finalFail(this, resultInfo, ex);
    }

  }

  /**
   * @param resultInfo
   * @param ex
   */
  private void handleRetry(final JobResultDO resultInfo, final Exception ex)
  {
    if (GLog.isTraceEnabled() == true) {
      /**
       * @logging
       * @reason
       * @action
       */
      GLog.trace(GenomeLogCategory.Scheduler, "Retry job", new LogJobEventAttribute(new JobEventImpl(job, job.getJobDefinition(), null,
          State.RETRY, scheduler)));
    }
    Date nextRun = job.getTrigger().updateAfterRun(scheduler, JobCompletion.EXPECTED_RETRY);
    job.setFireTime(nextRun);
    if (nextRun != null)
      job.setState(State.WAIT);
    else
      job.setState(State.STOP);
    jobStore.jobRetry(job, resultInfo, ex, scheduler);
  }

  private void handleRetryNextRun(final JobResultDO resultInfo, final Exception ex)
  {
    if (GLog.isTraceEnabled() == true) {
      /**
       * @logging
       * @reason
       * @action
       */
      GLog.trace(GenomeLogCategory.Scheduler, "Retry job", new LogJobEventAttribute(new JobEventImpl(job, job.getJobDefinition(), null,
          State.RETRY, scheduler)));
    }
    Date nextRun = job.getTrigger().updateAfterRun(scheduler, JobCompletion.JOB_COMPLETED);
    job.setFireTime(nextRun);
    if (nextRun != null)
      job.setState(State.WAIT);
    else
      job.setState(State.STOP);
    jobStore.jobRetry(job, resultInfo, ex, scheduler);
  }

  /**
   * @param resultInfo
   * @param ex
   */
  private void handleServiceError(final JobResultDO resultInfo, final ServiceUnavailableException ex)
  {
    if (GLog.isTraceEnabled() == true) {
      /**
       * @logging
       * @reason
       * @action
       */
      GLog.trace(GenomeLogCategory.Scheduler, "Service unavailable", new LogJobEventAttribute(new JobEventImpl(job, job.getJobDefinition(),
          null, State.RETRY, scheduler)));
    }

    Date nextRun = job.getTrigger().updateAfterRun(scheduler, JobCompletion.SERVICE_UNAVAILABLE);
    job.setFireTime(nextRun);
    if (nextRun != null)
      job.setState(State.WAIT);
    else
      job.setState(State.STOP);
    jobStore.serviceRetry(job, resultInfo, ex, scheduler);
    scheduler.pause(scheduler.getServiceRetryTime());
  }

  /**
   * @param resultInfo
   * @param result
   */
  private void completedJob(final JobResultDO resultInfo, final Object result)
  {
    boolean traceEnabled = GLog.isTraceEnabled();
    if (traceEnabled == true) {
      /**
       * @logging
       * @reason
       * @action
       */
      GLog.trace(GenomeLogCategory.Scheduler, "Job completed", new LogJobEventAttribute(new JobEventImpl(job, job.getJobDefinition(), null,
          State.FINISHED, scheduler)));
    }
    Date nextRun = job.getTrigger().updateAfterRun(scheduler, JobCompletion.JOB_COMPLETED);
    job.setFireTime(nextRun);
    if (nextRun != null)
      job.setState(State.WAIT);
    else
      job.setState(State.FINISHED);
    jobStore.jobCompleted(job, resultInfo, result, scheduler, nextRun);
    if (traceEnabled == true) {
      GLog.trace(GenomeLogCategory.Scheduler, "Job updated and finished", new LogJobEventAttribute(new JobEventImpl(job, job
          .getJobDefinition(), null, State.FINISHED, scheduler)));
    }
  }

  public Scheduler getScheduler()
  {
    return scheduler;
  }

  public void setScheduler(Scheduler scheduler)
  {
    this.scheduler = scheduler;
  }

  public TriggerJobDO getJob()
  {
    return job;
  }

  public void setJob(TriggerJobDO job)
  {
    this.job = job;
  }

  public JobStore getJobStore()
  {
    return jobStore;
  }

  public void setJobStore(JobStore jobStore)
  {
    this.jobStore = jobStore;
  }

  public FutureJob getFutureJob()
  {
    return futureJob;
  }

  public void setFutureJob(FutureJob futureJob)
  {
    this.futureJob = futureJob;
  }

}

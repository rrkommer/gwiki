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

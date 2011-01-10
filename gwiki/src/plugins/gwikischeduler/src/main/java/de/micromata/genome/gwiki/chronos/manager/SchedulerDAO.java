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
   * Create a Dispatcher instance
   * 
   * @param discriminator handle multiple domains
   * @return
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

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

/**
 * 
 * @author roger@micromata.de
 * 
 */
public interface JobRunnerFilter
{
  public Object filter(JobFilterChain jobFilterCain) throws Exception;

  /**
   * Return a priority for executing filters Lower value is lower priority. The Filter will applied later.
   * 
   * @return
   */
  public int getPriority();
}

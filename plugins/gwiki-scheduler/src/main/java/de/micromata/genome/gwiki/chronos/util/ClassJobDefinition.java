/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: ClassJobDefinition.java,v $
//
// Project   jchronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   27.12.2006
// Copyright Micromata 27.12.2006
//
// $Id: ClassJobDefinition.java,v 1.1 2007/02/09 09:57:50 roger Exp $
// $Revision: 1.1 $
// $Date: 2007/02/09 09:57:50 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.util;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import de.micromata.genome.gwiki.chronos.FutureJob;
import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.SchedulerException;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;
import de.micromata.genome.gwiki.chronos.logging.LogLevel;
import de.micromata.genome.gwiki.chronos.logging.LoggedRuntimeException;
import de.micromata.genome.gwiki.chronos.manager.GenomeClassJobFactory;

/**
 * TODO rrk, was ist der Unterschied zwischen das hier und {@link GenomeClassJobFactory}
 * 
 */
public class ClassJobDefinition implements JobDefinition
{

  private final Class< ? extends FutureJob> classToStart;

  private Map<String, Object> beanProperties = null;

  public Map<String, Object> getBeanProperties()
  {
    return beanProperties;
  }

  public void setBeanProperties(Map<String, Object> beanProperties)
  {
    this.beanProperties = beanProperties;
  }

  @SuppressWarnings("unchecked")
  public ClassJobDefinition(final String classNameToStart)
  {
    try {
      classToStart = (Class< ? extends FutureJob>) Class.forName(classNameToStart);
    } catch (Exception ex) {
      throw new RuntimeException("Failure loading class in ClassJobDefinition: " + ex.getMessage(), ex);
    }
  }

  public ClassJobDefinition(final Class< ? extends FutureJob> classToStart)
  {
    this.classToStart = classToStart;
  }

  protected void populate(FutureJob fj)
  {
    if (beanProperties != null)
      try {
        BeanUtils.populate(fj, beanProperties);
      } catch (Exception ex) {
        /**
         * @logging
         * @reason Die JobFactory konnte die bean properties nicht setzen
         * @action TechAdmin kontaktieren
         */
        throw new LoggedRuntimeException(ex, LogLevel.Error, GenomeLogCategory.Scheduler, "Cannot populate properties for bean: "
            + fj.getClass().getName()
            + ": "
            + ex.getMessage());
      }
  }

  protected FutureJob createInstance()
  {
    FutureJob futureJob;
    try {
      futureJob = classToStart.newInstance();
    } catch (final InstantiationException ex) {
      throw new SchedulerException(ex);
    } catch (final IllegalAccessException ex) {
      throw new SchedulerException(ex);
    }
    return futureJob;
  }

  public FutureJob getInstance()
  {
    FutureJob fj = createInstance();
    populate(fj);
    return fj;
  }

  @Override
  public String toString()
  {
    return "JobDefinition[class=" + classToStart.getCanonicalName() + "]";
  }

  public String getJobClassName()
  {
    return classToStart.getCanonicalName();
  }
}

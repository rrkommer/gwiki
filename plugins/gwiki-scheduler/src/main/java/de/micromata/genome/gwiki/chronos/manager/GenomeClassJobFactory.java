/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    lado@micromata.de
// Created   Feb 26, 2008
// Copyright Micromata Feb 26, 2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.manager;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import de.micromata.genome.gwiki.chronos.FutureJob;
import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;
import de.micromata.genome.gwiki.chronos.logging.LogLevel;
import de.micromata.genome.gwiki.chronos.logging.LoggedRuntimeException;
import de.micromata.genome.gwiki.chronos.util.ClassJobDefinition;

/**
 * TODO rrk, was ist der Unterschied zwischen das hier und {@link ClassJobDefinition}
 * 
 */
public class GenomeClassJobFactory implements JobDefinition
{

  ClassJobDefinition classJobDefinition;

  private Map<String, Object> beanProperties = null;

  public GenomeClassJobFactory(Class< ? extends FutureJob> job)
  {
    classJobDefinition = new ClassJobDefinition(job);
  }

  @SuppressWarnings("unchecked")
  public GenomeClassJobFactory(String classNameToStart)
  {
    try {
      Class< ? extends FutureJob> cls = (Class< ? extends FutureJob>) Thread.currentThread().getContextClassLoader().loadClass(
          classNameToStart);
      classJobDefinition = new ClassJobDefinition(cls);
    } catch (Exception ex) {
      /**
       * @logging
       * @reason Die JobFactory konnte nicht initialisiert werden
       * @action TechAdmin kontaktieren
       */
      throw new LoggedRuntimeException(ex, LogLevel.Error, GenomeLogCategory.Scheduler, "Cannot initialize GenomeClassJobFactory for: "
          + classNameToStart
          + ": "
          + ex.getMessage());
    }
  }

  public FutureJob getInstance()
  {
    FutureJob fj = classJobDefinition.getInstance();
    try {
      if (beanProperties != null)
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
    return fj;
  }

  public Map<String, Object> getBeanProperties()
  {
    return beanProperties;
  }

  public void setBeanProperties(Map<String, Object> beanProperties)
  {
    this.beanProperties = beanProperties;
  }

}

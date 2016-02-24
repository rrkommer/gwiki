////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
import de.micromata.genome.gwiki.chronos.util.ClassJobDefinition;
import de.micromata.genome.logging.GenomeLogCategory;
import de.micromata.genome.logging.LogLevel;
import de.micromata.genome.logging.LoggedRuntimeException;

/**
 * TODO rrk, was ist der Unterschied zwischen das hier und {@link ClassJobDefinition}
 * 
 */
public class GenomeClassJobFactory implements JobDefinition
{

  ClassJobDefinition classJobDefinition;

  private Map<String, Object> beanProperties = null;

  public GenomeClassJobFactory(Class<? extends FutureJob> job)
  {
    classJobDefinition = new ClassJobDefinition(job);
  }

  @SuppressWarnings("unchecked")
  public GenomeClassJobFactory(String classNameToStart)
  {
    try {
      Class<? extends FutureJob> cls = (Class<? extends FutureJob>) Thread.currentThread().getContextClassLoader()
          .loadClass(classNameToStart);
      classJobDefinition = new ClassJobDefinition(cls);
    } catch (Exception ex) {
      /**
       * @logging
       * @reason Die JobFactory konnte nicht initialisiert werden
       * @action TechAdmin kontaktieren
       */
      throw new LoggedRuntimeException(ex, LogLevel.Error, GenomeLogCategory.Scheduler,
          "Cannot initialize GenomeClassJobFactory for: "
              + classNameToStart
              + ": "
              + ex.getMessage());
    }
  }

  @Override
  public FutureJob getInstance()
  {
    FutureJob fj = classJobDefinition.getInstance();
    try {
      if (beanProperties != null) {
        BeanUtils.populate(fj, beanProperties);
      }
    } catch (Exception ex) {
      /**
       * @logging
       * @reason Die JobFactory konnte die bean properties nicht setzen
       * @action TechAdmin kontaktieren
       */
      throw new LoggedRuntimeException(ex, LogLevel.Error, GenomeLogCategory.Scheduler,
          "Cannot populate properties for bean: "
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

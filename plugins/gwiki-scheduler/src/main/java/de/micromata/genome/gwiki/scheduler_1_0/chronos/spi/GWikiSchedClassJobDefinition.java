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

import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;

import de.micromata.genome.gwiki.chronos.FutureJob;
import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.model.GWikiSchedulerJob;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.scheduler_1_0.jobs.GWikiSchedJobAdapter;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.text.PipeValueList;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSchedClassJobDefinition implements JobDefinition
{
  protected final String classNameToStart;

  protected Map<String, String> beanProperties = null;

  public GWikiSchedClassJobDefinition(final String classNameToStart, Map<String, String> beanProperties)
  {
    this.classNameToStart = classNameToStart;
    this.beanProperties = beanProperties;
  }

  private void mapProperties(Object o) throws Exception
  {
    if (beanProperties != null && beanProperties.isEmpty() == false) {
      BeanUtilsBean.getInstance().populate(o, beanProperties);
    }
  }

  public FutureJob getInstance()
  {
    return GWikiWeb.get().runInPluginContext(new CallableX<FutureJob, RuntimeException>() {

      public FutureJob call() throws RuntimeException
      {

        try {

          Class c = Thread.currentThread().getContextClassLoader().loadClass(classNameToStart);
          Object o = c.newInstance();
          mapProperties(o);
          if (o instanceof FutureJob) {
            return (FutureJob) o;
          } else if (o instanceof GWikiSchedulerJob) {
            return new GWikiSchedJobAdapter((GWikiSchedulerJob) o);
          } else {
            throw new RuntimeException("Unknown job type to create: " + o.getClass());

          }
        } catch (RuntimeException ex) {
          throw ex;
        } catch (Exception ex) {
          throw new RuntimeException("Failure loading class in ClassJobDefinition: " + ex.getMessage(), ex);
        }
      }
    });
  }

  public String serialize()
  {
    if (beanProperties == null || beanProperties.isEmpty() == true) {
      return classNameToStart;
    }
    return classNameToStart + "|" + PipeValueList.encode(beanProperties);
  }

  public static GWikiSchedClassJobDefinition deserialize(String line)
  {
    int idx = line.indexOf('|');
    if (idx == -1) {
      return new GWikiSchedClassJobDefinition(line, null);
    }
    String cn = line.substring(0, idx);
    String args = line.substring(idx + 1);
    return new GWikiSchedClassJobDefinition(cn, PipeValueList.decode(args));

  }

  public Map<String, String> getBeanProperties()
  {
    return beanProperties;
  }

  public void setBeanProperties(Map<String, String> beanProperties)
  {
    this.beanProperties = beanProperties;
  }

  public String getClassNameToStart()
  {
    return classNameToStart;
  }

}

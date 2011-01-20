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

import de.micromata.genome.gwiki.chronos.FutureJob;
import de.micromata.genome.gwiki.chronos.util.ClassJobDefinition;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSchedClassJobDefinition extends ClassJobDefinition
{
  protected static Class< ? extends FutureJob> loadClass(final String className)
  {
    return GWikiWeb.get().runInPluginContext(new CallableX<Class< ? extends FutureJob>, RuntimeException>() {

      public Class< ? extends FutureJob> call() throws RuntimeException
      {
        try {
          return (Class< ? extends FutureJob>) Thread.currentThread().getContextClassLoader().loadClass(className);
          // return (Class< ? extends FutureJob>) Class.forName(className);
        } catch (Exception ex) {
          throw new RuntimeException("Failure loading class in ClassJobDefinition: " + ex.getMessage(), ex);
        }
      }
    });
  }

  public GWikiSchedClassJobDefinition(final String classNameToStart)
  {
    super(loadClass(classNameToStart));
  }
}

//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.chronos.spi.jdbc;

import java.util.Map;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.util.ClassJobDefinition;
import de.micromata.genome.gwiki.scheduler_1_0.chronos.spi.GWikiSchedClassJobDefinition;
import de.micromata.genome.util.text.PipeValueList;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SerializationUtil
{
  public static String serializeJobDefinition(Object obj)
  {
    if (obj instanceof GWikiSchedClassJobDefinition) {
      return ((GWikiSchedClassJobDefinition) obj).serialize();
    }
    if (obj instanceof ClassJobDefinition) {
      return ((ClassJobDefinition) obj).getJobClassName();
    }
    return obj.getClass().getCanonicalName();
  }

  @SuppressWarnings("unchecked")
  public static String serializeJobArguments(Object obj)
  {
    if (obj instanceof Map) {
      return PipeValueList.encode((Map) obj);
    }
    if (obj instanceof String) {
      return (String) obj;
    }
    return null;
  }

  public static Object deserializeJobArguments(String s)
  {
    return s;
    // if (s == null) {
    // return new HashMap<String, String>();
    // }
    // return PipeValueList.decode(s);
  }

  public static JobDefinition deserializeJobDefinition(String s)
  {
    return GWikiSchedClassJobDefinition.deserialize(s);
  }
}

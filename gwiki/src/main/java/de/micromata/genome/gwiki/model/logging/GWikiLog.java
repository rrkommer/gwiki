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

package de.micromata.genome.gwiki.model.logging;

import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogExceptionAttribute;

/**
 * Static helper function delegates all calles to GWikiLogging.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiLog
{

  public static void warn(String message)
  {
    GLog.warn(GWikiLogCategory.Wiki, message);
  }

  public static void error(String message)
  {
    GLog.error(GWikiLogCategory.Wiki, message);
  }

  public static void warn(String message, Throwable ex)
  {
    GLog.warn(GWikiLogCategory.Wiki, message, new LogExceptionAttribute(ex));
  }

  public static void error(String message, Throwable ex)
  {
    GLog.error(GWikiLogCategory.Wiki, message, new LogExceptionAttribute(ex));
  }

}

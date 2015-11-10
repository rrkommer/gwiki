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

package de.micromata.genome.gwiki.model;

import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Interface to start jobs asynchron.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiSchedulerProvider
{
  /**
   * start async job. If job cannot be started, because scheduler is bussy, returns false. An error message will be added to wikiContext
   * 
   * @return
   */
  boolean execAsyncOne(GWikiContext wikiContext, Class< ? extends GWikiSchedulerJob> callback, Map<String, String> args);

  /**
   * Execute multiple instances of job
   * 
   * @param wikiContext
   * @param callback
   * @param args
   * @return
   */
  boolean execAsyncMultiple(final GWikiContext wikiContext, final Class< ? extends GWikiSchedulerJob> callback,
      final Map<String, String> args);
}

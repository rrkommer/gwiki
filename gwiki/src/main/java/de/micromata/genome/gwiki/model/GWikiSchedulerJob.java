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

import java.io.Serializable;
import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Interface for a job executed in a thread.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiSchedulerJob extends Serializable
{
  public Object call(Map<String, String> args);

  public void call();

  public GWikiContext getWikiContext();

  public void setWikiContext(GWikiContext wikiContext);

  public Map<String, String> getArgs();

  public void setArgs(Map<String, String> args);

}

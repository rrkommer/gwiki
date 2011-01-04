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
package de.micromata.genome.gwiki.model;

import java.util.Date;
import java.util.List;

import de.micromata.genome.util.types.Pair;

/**
 * Interface to view Log entries
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiLogViewer
{
  public interface Callback
  {
    void found(Date date, int logLevel, String message, List<Pair<String, String>> params);
  }

  void grep(Date start, Date end, Integer logLevel, String message, List<Pair<String, String>> params, int offset, int maxitems, Callback cb);
}

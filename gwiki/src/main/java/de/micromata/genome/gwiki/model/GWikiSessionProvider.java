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

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Interface to deal with sessions. Because some runtime environments - like Genome Plugins - needs some preparation to read/write session
 * objects, this interface will encapsulate the implementation.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiSessionProvider
{
  Object getSessionAttribute(GWikiContext wikiContext, String key);

  void setSessionAttribute(GWikiContext wikiContext, String key, Serializable object);

  void removeSessionAttribute(GWikiContext wikiContext, String key);

  void clearSessionAttributes(GWikiContext wikiContext);
}

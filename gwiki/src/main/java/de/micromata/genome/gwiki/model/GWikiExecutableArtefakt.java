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
 * Artefakt can display/render to html.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiExecutableArtefakt<T extends Serializable> extends GWikiArtefakt<T>
{
  /**
   * Will be called before page will be rendered.
   * 
   * Usefull to add required css/or js or set http header.
   * 
   * @param wikiContext
   */
  public void prepareHeader(GWikiContext wikiContext);

  /**
   * 
   * @param ctx
   * @return true if continue processing. Otherwise stop processing.
   */
  public boolean render(GWikiContext wikiContext);
}

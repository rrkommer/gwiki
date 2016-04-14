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

package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Provides Mapping of mime type
 * 
 * @author roger
 * 
 */
public interface GWikiMimeTypeProvider
{
  /**
   * Provides mime type for given element
   * 
   * @param wikiContext
   * @param ei
   * @return the mime type application/format if found, otherwise null
   */
  public String getMimeType(GWikiContext wikiContext, GWikiElementInfo el);

  /**
   * Provides mime type for given page uri
   * 
   * @param wikiContext
   * @param uri page id
   * @return the mime type application/format if found, otherwise null
   */
  public String getMimeType(GWikiContext wikiContext, String uri);

}

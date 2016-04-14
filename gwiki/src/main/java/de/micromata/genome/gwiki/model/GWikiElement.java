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

import java.io.Serializable;
import java.util.Map;

import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;

/**
 * All items (page, attachments, config, etc.) are a GWikiElement.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiElement extends Serializable
{
  /**
   * The head information of this element.
   * 
   * @return
   */
  GWikiElementInfo getElementInfo();

  /**
   * Setter for GWikiElementInfo
   * 
   * @param ei
   */
  void setElementInfo(GWikiElementInfo ei);

  /**
   * Get all parts/Artefakts of this elements.
   * 
   * @param map
   */
  void collectParts(Map<String, GWikiArtefakt< ? >> map);

  /**
   * Get the main artefakt.
   * 
   * @return null if not exists
   */
  GWikiArtefakt< ? > getMainPart();

  /**
   * 
   * @param partName
   * @return null if not exists
   */
  GWikiArtefakt< ? > getPart(String partName);

  /**
   * The GWikiMetaTemplate describes the type of this element.
   * 
   * @return
   */
  GWikiMetaTemplate getMetaTemplate();

  /**
   * Called before rending content to set http or html header.
   * 
   * @param wikiContext
   */
  void prepareHeader(GWikiContext wikiContext);

  /**
   * Render/Serve this element. Basically has the same functionality of a servlet.
   * 
   * @param ctx
   */
  void serve(GWikiContext ctx);

  /**
   * Store part/artefakt information from editors into internal element structures.
   * 
   * @param ctx
   * @param editors
   */
  void saveParts(GWikiContext ctx, Map<String, GWikiEditorArtefakt< ? >> editors);
}

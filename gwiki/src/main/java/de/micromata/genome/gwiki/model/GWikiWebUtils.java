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

import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * GWikiWeb static utils.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWebUtils
{
  public static GWikiElement createNewElement(GWikiContext wikiContext, String id, String metaTemplateId, String title)
  {
    GWikiProps props = new GWikiSettingsProps();
    GWikiMetaTemplate template = wikiContext.getWikiWeb().findMetaTemplate(metaTemplateId);
    // props.setStringValue(GWikiPropKeys.TYPE, template.getElementType());
    props.setStringValue(GWikiPropKeys.WIKIMETATEMPLATE, metaTemplateId);
    props.setStringValue(GWikiPropKeys.TITLE, title);
    GWikiElementInfo neiei = new GWikiElementInfo(props, template);
    neiei.setId(id);
    GWikiElement elementToEdit = wikiContext.getWikiWeb().getStorage().createElement(neiei);
    return elementToEdit;
  }

}

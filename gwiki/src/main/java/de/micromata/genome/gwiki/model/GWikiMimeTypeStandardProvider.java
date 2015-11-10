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

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.web.MimeUtils;

/**
 * Standard provider.
 * 
 * Uses lookups in following order:
 * <ul>
 * <li>Element setting CONTENTTYPE</li>
 * <li>MetaTemplates contentType</li>
 * <li>admin/config/MimeTypeConfig matches</li>
 * <li>MimeUtils.getMimeTypeFromFile</li>
 * </ul>
 * 
 * @author roger
 * 
 */
public class GWikiMimeTypeStandardProvider implements GWikiMimeTypeProvider
{

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiMimeTypeProvider#getMimeType(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.model.GWikiElementInfo)
   */
  @Override
  public String getMimeType(GWikiContext wikiContext, GWikiElement el)
  {
    String contt = el.getElementInfo().getProps().getStringValue(GWikiPropKeys.CONTENTYPE);
    if (StringUtils.isNotEmpty(contt) == true) {
      return contt;
    }
    contt = el.getElementInfo().getMetaTemplate().getContentType();
    if (StringUtils.isNotEmpty(contt) == true) {
      return contt;
    }
    return getMimeType(wikiContext, el.getElementInfo().getId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiMimeTypeProvider#getMimeType(de.micromata.genome.gwiki.page.GWikiContext, java.lang.String)
   */
  @Override
  public String getMimeType(GWikiContext wikiContext, String uri)
  {
    String configId = "admin/config/MimeTypeConfig";
    GWikiElement configEl = wikiContext.getWikiWeb().findElement(configId);
    if (configEl == null) {
      return MimeUtils.getMimeTypeFromFile(uri);
    }
    GWikiI18NArtefakt p = (GWikiI18NArtefakt) configEl.getMainPart();
    if (p != null && p.getCompiledObject() != null) {
      for (Map.Entry<String, String> me : p.getCompiledObject().entrySet()) {
        if (uri.endsWith(me.getKey()) == true) {
          return me.getValue();
        }
      }
    }
    String contt = MimeUtils.getMimeTypeFromFile(uri);
    if (contt != null) {
      return contt;
    }
    GWikiLog.note("Cannot find content type for pageId: " + uri);
    return null;
  }

}

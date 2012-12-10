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
package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.io.IOException;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Support ETags in header. The etag will be build by modifiedat and version number. If browser sends "If-None-Match" header, it compares to
 * the etag.
 * 
 * @author roger
 * 
 */
public class GWikiServeEtagElementFilter implements GWikiServeElementFilter
{
  public static String createEtag(GWikiContext wikiContext, GWikiElement el)
  {
    if (el == null || el.getElementInfo().getMetaTemplate().isCachable() == false) {
      return null;
    }
    String ret = el.getElementInfo().getProps().getStringValue(GWikiPropKeys.MODIFIEDAT);
    String version = el.getElementInfo().getProps().getStringValue(GWikiPropKeys.VERSION);
    if (ret == null) {
      return version;
    }
    if (version == null) {
      return ret;
    }
    return ret + version;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain, GWikiServeElementFilterEvent event)
  {
    GWikiElement el = event.getElement();
    GWikiContext wikiContext = event.getWikiContext();
    String etag = createEtag(wikiContext, el);
    if (el != null && etag != null) {
      // If-Modified-Since: Tue, 12 Dec 2006 03:03:59 GMT
      // If-None-Match: "10c24bc-4ab-457e1c1f"
      String ifnm = wikiContext.getRequest().getHeader("If-None-Match");
      String ifnomod = wikiContext.getRequest().getHeader("If-Modified-Since");
      if (etag.equals(ifnm) == true) {
        try {
          wikiContext.getResponse().sendError(304, "Not modified");
        } catch (IOException ex) {
          // ignore...
        }
        return null;
      }
    }

    if (etag != null) {
      wikiContext.getResponse().addHeader("ETag", etag);
    }
    return chain.nextFilter(event);
  }
}

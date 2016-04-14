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

package de.micromata.genome.gwiki.wicket.util;

import org.apache.commons.lang.Validate;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPage;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiChunkMacro;

/**
 * Contains utility methods to access GWiki
 * 
 * @author fnaujoks
 * 
 */
public abstract class GWikiUtils
{

  /**
   * Retrieves a given GWiki page, or, if not null, a GWiki part, or, if not null, a GWiki chunk. Will cause an exception if no page with
   * the given id is found.
   * 
   * @param pageId GWiki page id, may not be null or empty
   * @param partName part name, may be null
   * @param chunk chunk name, may be null
   * @return GWiki page or part or chunk
   */
  public static String getWikiPage(String pageId, String partName, String chunk)
  {
    Validate.notEmpty(pageId);
    GWikiWeb wikiWeb = GWikiWeb.getWiki();
    GWikiElement el = wikiWeb.getElement(pageId);

    GWikiStandaloneContext wikiContext = GWikiStandaloneContext.create();
    if (partName != null) {
      wikiContext.setRequestAttribute(GWikiWikiPage.REQUESTATTR_GWIKIPART, partName);
    }
    if (chunk != null) {
      wikiContext.setRequestAttribute(GWikiChunkMacro.REQUESTATTR_GWIKICHUNK, chunk);
    }
    try {
      GWikiContext.setCurrent(wikiContext);
      el.serve(wikiContext);
      String sout = wikiContext.getOutString();
      return sout;
    } finally {
      GWikiContext.setCurrent(null);
    }
  }

  /**
   * Retrieves a given GWiki page, or, if not null, a GWiki part, or, if not null, a GWiki chunk. Will return null if neither is found.
   * 
   * @param pageId GWiki page id, may not be null or empty
   * @param partName part name, may be null
   * @param chunk chunk name, may be null
   * @return GWiki page or part or chunk, null if neither was found.
   */
  public static String findWikiPage(String pageId, String partName, String chunk)
  {
    GWikiWeb wikiWeb = GWikiWeb.getWiki();
    GWikiElement el = wikiWeb.findElement(pageId);
    if (el == null) {
      GWikiContext.setCurrent(null);
      return null;
    }

    GWikiStandaloneContext wikiContext = GWikiStandaloneContext.create();
    if (partName != null) {
      wikiContext.setRequestAttribute(GWikiWikiPage.REQUESTATTR_GWIKIPART, partName);
    }
    if (chunk != null) {
      wikiContext.setRequestAttribute(GWikiChunkMacro.REQUESTATTR_GWIKICHUNK, chunk);
    }
    try {
      GWikiContext.setCurrent(wikiContext);
      el.serve(wikiContext);
      String sout = wikiContext.getOutString();
      return sout;
    } finally {
      GWikiContext.setCurrent(null);
    }
  }

  /**
   * Retrieves the title of a GWiki page, will return null if no page with the given id is found.
   * 
   * @param pageId GWiki page id, may not be null or empty
   * @return title of the given GWiki page or null if no page matching the id is found
   */
  public static String findWikiPageTitle(String pageId)
  {
    Validate.notEmpty(pageId);
    GWikiWeb wikiWeb = GWikiWeb.getWiki();
    GWikiElementInfo elementInfo = wikiWeb.findElementInfo(pageId);
    if (elementInfo == null) {
      GWikiContext.setCurrent(null);
      return null;
    }
    return elementInfo.getTitle();
  }
}

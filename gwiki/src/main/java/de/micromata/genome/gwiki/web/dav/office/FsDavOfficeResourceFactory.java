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

package de.micromata.genome.gwiki.web.dav.office;

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.web.dav.FsDavResourceFactory;
import de.micromata.genome.gwiki.web.dav.GDirectoryResource;
import de.micromata.genome.gwiki.web.dav.GFileResource;

/**
 * Experimental DAV service for MS Word online editing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsDavOfficeResourceFactory implements ResourceFactory
{
  private GWikiWeb wikiWeb;

  private FsDavResourceFactory fileResourceFactory;

  public FsDavOfficeResourceFactory(GWikiWeb wikiWeb, FsDavResourceFactory fsfac)
  {
    this.wikiWeb = wikiWeb;
    this.fileResourceFactory = fsfac;
  }

  protected GOfficeFileResource getElementForPath(String host, String path)
  {

    if (path.endsWith("MainPage.doc") == false && path.endsWith("MainPage.html") == false) {
      return null;
    }

    String pageIdPath;
    if (path.endsWith("MainPage.doc") == true) {
      pageIdPath = path.substring(0, path.length() - "MainPage.doc".length());
    } else {
      pageIdPath = path.substring(0, path.length() - "MainPage.html".length());
    }
    String realPath = pageIdPath + "MainPage.html";
    Resource res = fileResourceFactory.getResource(host, realPath);
    if (res == null) {
      return null;
    }
    if ((res instanceof GFileResource) == false) {
      return null;
    }
    String servPath = GWikiContext.getCurrent().getRequest().getServletPath();
    String contextPath = GWikiContext.getCurrent().getRequest().getContextPath();
    String pathPrefix = contextPath + servPath;
    String pageId = pageIdPath.substring(pathPrefix.length() + 1);
    GOfficeFileResource fr = new GOfficeFileResource(this, (GFileResource) res);

    GWikiElement el = wikiWeb.findElement(pageId);
    if (el == null) {
      return null;
    }
    fr.setElement(el);
    fr.setPartName("MainPage");

    return fr;
  }

  public Resource getResource(String host, String path)
  {
    GOfficeFileResource el = getElementForPath(host, path);
    if (el != null) {
      return el;
    }
    Resource res = fileResourceFactory.getResource(host, path);
    if (res instanceof GDirectoryResource) {
      return res;
    }
    return null;
  }

  public String getSupportedLevels()
  {
    return "1,2";
  }

  public GWikiWeb getWikiWeb()
  {
    return wikiWeb;
  }

  public void setWikiWeb(GWikiWeb wikiWeb)
  {
    this.wikiWeb = wikiWeb;
  }

  public FsDavResourceFactory getFileResourceFactory()
  {
    return fileResourceFactory;
  }

  public void setFileResourceFactory(FsDavResourceFactory fileResourceFactory)
  {
    this.fileResourceFactory = fileResourceFactory;
  }

}

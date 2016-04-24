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

package de.micromata.genome.gwiki.web.dav.office;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.web.dav.GFileResource;

/**
 * Experimental DAV service for MS Word online editing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GOfficeFileResource extends GFileResource
{
  private GWikiElement element;

  private String partName;

  private GWikiFragment fragment;

  private FsDavOfficeResourceFactory officeResFac;

  private GFileResource fileObject;

  private static String htmlPrefix = "<html><body><!-- GWIKI START -->";

  private static String htmlSuffix = "<!-- GWIKI END --></body></html>";

  public GOfficeFileResource(FsDavOfficeResourceFactory resourceFactory, GFileResource fileObject)
  {
    super(resourceFactory.getFileResourceFactory(), fileObject.getFileObject());
    this.officeResFac = resourceFactory;
    this.fileObject = fileObject;
  }

  @Override
  public Date getModifiedDate()
  {
    // hack, damit die Datei nue geladen wird.
    return new Date();
  }

  @Override
  public Long getContentLength()
  {
    Long ret = fileObject.getContentLength();
    if (ret == null) {
      return null;
    }
    return htmlPrefix.length() + ret + htmlSuffix.length();
  }

  @Override
  public String getContentType(String accepts)
  {
    return super.getContentType(accepts);
  }

  @Override
  public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException,
      NotAuthorizedException
  {
    out.write(htmlPrefix.getBytes());
    fileObject.sendContent(out, range, params, contentType);
    out.write(htmlSuffix.getBytes());
  }

  public GWikiElement getElement()
  {
    return element;
  }

  public void setElement(GWikiElement element)
  {
    this.element = element;
  }

  public String getPartName()
  {
    return partName;
  }

  public void setPartName(String partName)
  {
    this.partName = partName;
  }

  public GWikiFragment getFragment()
  {
    return fragment;
  }

  public void setFragment(GWikiFragment fragment)
  {
    this.fragment = fragment;
  }

  @Override
  public void delete()
  {
  }

}

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

package de.micromata.genome.gwiki.controls;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * ActionBean to show the upload and screenshot applet.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUploadWindowActionBean extends ActionBeanBase
{

  private String pageId;

  private String parentPageId;

  private String homeUrl;

  private String appletUrl;

  private boolean storeTmpFile;

  /**
   * empty or img
   */
  private String allowedFileType;

  /**
   * Javascript callback.
   */
  private String jscb;

  public Object onInit()
  {
    appletUrl = "/edit/gwiki-uploader-applet-0.3.1.jar";
    homeUrl = wikiContext.getResponse().encodeURL(wikiContext.globalUrl("/edit/UploadAttachment"));
    return null;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getHomeUrl()
  {
    return homeUrl;
  }

  public void setHomeUrl(String callbackUrl)
  {
    this.homeUrl = callbackUrl;
  }

  public String getAppletUrl()
  {
    return appletUrl;
  }

  public void setAppletUrl(String appletUrl)
  {
    this.appletUrl = appletUrl;
  }

  public boolean isStoreTmpFile()
  {
    return storeTmpFile;
  }

  public void setStoreTmpFile(boolean storeTmpFile)
  {
    this.storeTmpFile = storeTmpFile;
  }

  public String getAllowedFileType()
  {
    return allowedFileType;
  }

  public void setAllowedFileType(String fileType)
  {
    this.allowedFileType = fileType;
  }

  public String getJscb()
  {
    return jscb;
  }

  public void setJscb(String jcb)
  {
    this.jscb = jcb;
  }

  public String getParentPageId()
  {
    return parentPageId;
  }

  public void setParentPageId(String parentPageId)
  {
    this.parentPageId = parentPageId;
  }
}

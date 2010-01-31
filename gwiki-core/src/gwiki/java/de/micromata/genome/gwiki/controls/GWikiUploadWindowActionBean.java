/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   15.12.2009
// Copyright Micromata 15.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * ActionBean to show the upload and screenshot applet.
 * 
 * @author roger@micromata.de
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
    appletUrl = "/edit/gwiki-image-uploader1.0.jar";
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

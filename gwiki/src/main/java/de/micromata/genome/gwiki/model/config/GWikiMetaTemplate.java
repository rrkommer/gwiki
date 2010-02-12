/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * A meta template defines the internal structure of a GWikiElement.
 * 
 * @author roger
 * 
 */
public class GWikiMetaTemplate implements Serializable
{

  private static final long serialVersionUID = 7116843033071027993L;

  /**
   * This page id
   */
  private String pageId;

  private String elementType;

  /**
   * right to create or/and edit page.
   */
  private String requiredViewRight;

  private String requiredEditRight;

  /**
   * The element can directly be viewed.
   */
  private boolean viewable = true;

  private boolean cachable = true;

  private boolean noSearchIndex = false;

  private Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();

  /**
   * Welche Seite soll als Template verwendet werden.
   */
  private String copyFromPageId;

  /**
   * Kein Template, mit dem man neue Seiten erstellen kann.
   */
  private boolean noNewPage = false;

  /**
   * In case of overwrite, do not archive this.
   */
  private boolean noArchiv = false;

  /**
   * Standard cache time for this element type
   */
  private long elementLifeTime = TimeInMillis.HOUR;

  public GWikiMetaTemplate()
  {

  }

  public GWikiMetaTemplate(String pageId)
  {
    this.pageId = pageId;
  }

  public Map<String, GWikiArtefakt< ? >> getParts()
  {
    return parts;
  }

  public void setParts(Map<String, GWikiArtefakt< ? >> parts)
  {
    this.parts = parts;
  }

  public String getElementType()
  {
    return elementType;
  }

  public void setElementType(String elementType)
  {
    this.elementType = elementType;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getCopyFromPageId()
  {
    return copyFromPageId;
  }

  public void setCopyFromPageId(String copyFromPageId)
  {
    this.copyFromPageId = copyFromPageId;
  }

  public boolean isNoNewPage()
  {
    return noNewPage;
  }

  public void setNoNewPage(boolean noNewPage)
  {
    this.noNewPage = noNewPage;
  }

  public boolean isViewable()
  {
    return viewable;
  }

  public void setViewable(boolean viewable)
  {
    this.viewable = viewable;
  }

  public boolean isCachable()
  {
    return cachable;
  }

  public void setCachable(boolean cachable)
  {
    this.cachable = cachable;
  }

  public boolean isNoSearchIndex()
  {
    return noSearchIndex;
  }

  public void setNoSearchIndex(boolean noSearchIndex)
  {
    this.noSearchIndex = noSearchIndex;
  }

  public String getRequiredViewRight()
  {
    return requiredViewRight;
  }

  public void setRequiredViewRight(String requiredViewRight)
  {
    this.requiredViewRight = requiredViewRight;
  }

  public String getRequiredEditRight()
  {
    return requiredEditRight;
  }

  public void setRequiredEditRight(String requiredEditRight)
  {
    this.requiredEditRight = requiredEditRight;
  }

  public boolean isNoArchiv()
  {
    return noArchiv;
  }

  public void setNoArchiv(boolean noArchiv)
  {
    this.noArchiv = noArchiv;
  }

  public long getElementLifeTime()
  {
    return elementLifeTime;
  }

  public void setElementLifeTime(long lifeTime)
  {
    this.elementLifeTime = lifeTime;
  }

}

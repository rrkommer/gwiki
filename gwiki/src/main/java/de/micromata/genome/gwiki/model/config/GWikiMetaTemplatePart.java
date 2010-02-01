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

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiWeb;

public class GWikiMetaTemplatePart implements Serializable
{
  /**
   * may be null, if in the page itself.
   */
  private String pageId;

  private String artefaktType;

  private String artefaktClass;

  public GWikiArtefakt createArtefakt()
  {
    String clsName = null;
    if (StringUtils.isNotEmpty(artefaktClass) == true) {
      clsName = artefaktClass;
    } else if (StringUtils.isNotEmpty(artefaktType) == true) {
      clsName = GWikiWeb.get().getStorage().getArtefaktClassNameFromType(artefaktType);
    }
    // TODO pageId
    if (clsName == null) {
      throw new RuntimeException("No artefakt type found");
    }
    try {
      return (GWikiArtefakt) Class.forName(clsName).newInstance();
    } catch (Throwable ex) {
      throw new RuntimeException("failed to load artefakt class " + clsName + "; " + ex.getMessage(), ex);
    }
  }

  public String getArtefaktType()
  {
    return artefaktType;
  }

  public void setArtefaktType(String artefaktType)
  {
    this.artefaktType = artefaktType;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getArtefaktClass()
  {
    return artefaktClass;
  }

  public void setArtefaktClass(String artefaktClass)
  {
    this.artefaktClass = artefaktClass;
  }
}

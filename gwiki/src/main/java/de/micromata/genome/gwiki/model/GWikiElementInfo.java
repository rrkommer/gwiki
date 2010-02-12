/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.10.2009
// Copyright Micromata 19.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Core information about a element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementInfo implements Serializable, GWikiPropKeys
{

  private static final long serialVersionUID = 2444973005532215921L;

  /**
   * id is the file name of the element.
   */
  private String id;

  private GWikiProps props;

  /**
   * Zeitpunkt des letzten Ladens.
   */
  private long loadedTimeStamp = System.currentTimeMillis();

  /**
   * reference to the meta template.
   * 
   * may be null.
   */
  private GWikiMetaTemplate metaTemplate;

  public GWikiElementInfo(GWikiProps props, GWikiMetaTemplate metaTemplate)
  {
    this.props = props;
    this.metaTemplate = metaTemplate;
  }

  public GWikiElementInfo(GWikiElementInfo other)
  {
    this.props = new GWikiProps(other.props);
    this.id = other.id;
    this.metaTemplate = other.metaTemplate;
  }

  public boolean equals(Object oo)
  {
    if ((oo instanceof GWikiElementInfo) == false)
      return false;
    GWikiElementInfo other = (GWikiElementInfo) oo;
    return id.equals(other.getId());
  }

  public String toString()
  {
    return id;
  }

  public String getParentId()
  {
    return props.getStringValue(GWikiPropKeys.PARENTPAGE);
  }

  public GWikiElementInfo getParent(GWikiContext wikiContext)
  {
    String pif = getParentId();
    if (StringUtils.isEmpty(pif) == true) {
      return null;
    }
    return wikiContext.getWikiWeb().findElementInfo(pif);
  }

  public String getTitle()
  {
    String title = props.getStringValue(GWikiPropKeys.TITLE);
    if (StringUtils.isNotEmpty(title) == true)
      return title;
    return id;
  }

  public boolean isViewable()
  {
    if (props.getBooleanValue(GWikiPropKeys.NOVIEW) == true)
      return false;
    if (metaTemplate == null) {
      return true;
    }
    return metaTemplate.isViewable();
  }

  /**
   * 
   * @return -1 if order is not definied
   */
  public int getOrder()
  {
    return props.getIntValue(GWikiPropKeys.ORDER, -1);
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public GWikiProps getProps()
  {
    return props;
  }

  public void setProps(GWikiProps props)
  {
    this.props = props;
  }

  public Date getModifiedAt()
  {
    return props.getDateValue(MODIFIEDAT);
  }

  public String getModifiedBy()
  {
    return props.getStringValue(MODIFIEDBY);
  }

  public Date getCreatedAt()
  {
    return props.getDateValue(CREATEDAT);
  }

  public String getCreatedBy()
  {
    return props.getStringValue(CREATEDBY);
  }

  public String getType()
  {
    return props.getStringValue(TYPE);
  }

  public void setType(String type)
  {
    props.setStringValue(TYPE, type);
  }

  public boolean isIndexed()
  {
    if (props.getBooleanValue(NOINDEX) == true) {
      return false;
    }
    if (metaTemplate != null) {
      if (metaTemplate.isNoSearchIndex() == true) {
        return false;
      }
    }
    return true;
  }

  public String getRecStringValue(String key, String defaultValue, GWikiContext wikiContext)
  {
    final String val = props.getStringValue(key);
    if (StringUtils.isNotEmpty(val) == true) {
      return val;
    }
    final GWikiElementInfo pi = getParent(wikiContext);
    if (pi != null) {
      return pi.getRecStringValue(key, defaultValue, wikiContext);
    }
    return defaultValue;
  }

  /**
   * get the language of the element. return null if no language is defined.
   * 
   * @param wikiContext
   * @return
   */
  public String getLang(GWikiContext wikiContext)
  {
    return getRecStringValue(LANG, null, wikiContext);
  }

  /**
   * Getter
   * 
   * @return
   */
  public String getWikiSpace()
  {
    return props.getStringValue(WIKISPACE);
  }

  /**
   * Allways return a valid space identifier.
   * 
   * If no space is defined, return "GLOBAL"
   * 
   * @param wikiContext
   * @return
   */
  public String getWikiSpace(GWikiContext wikiContext)
  {
    return getRecStringValue(WIKISPACE, "GLOBAL", wikiContext);
  }

  public long getLoadedTimeStamp()
  {
    return loadedTimeStamp;
  }

  public void setLoadedTimeStamp(long loadedTimeStamp)
  {
    this.loadedTimeStamp = loadedTimeStamp;
  }

  public GWikiMetaTemplate getMetaTemplate()
  {
    return metaTemplate;
  }

  public void setMetaTemplate(GWikiMetaTemplate metaTemplate)
  {
    this.metaTemplate = metaTemplate;
  }

}

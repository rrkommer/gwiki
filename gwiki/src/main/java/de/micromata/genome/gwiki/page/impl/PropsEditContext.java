/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.11.2009
// Copyright Micromata 19.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.ScriptUtils;

/**
 * Context for render a property editor.
 * 
 * @author roger
 * 
 */
public class PropsEditContext
{
  private GWikiContext wikiContext;

  private GWikiPropsArtefakt propsArtefakt;

  private String partName;

  private GWikiPropsDescriptorValue propDescriptor;

  private Object controlerBean = null;

  public PropsEditContext()
  {

  }

  public PropsEditContext(GWikiContext wikiContext, GWikiPropsArtefakt propsArtefakt, String partName,
      GWikiPropsDescriptorValue propDescriptor)
  {
    this.wikiContext = wikiContext;
    this.propDescriptor = propDescriptor;
    this.propsArtefakt = propsArtefakt;
    this.partName = partName;
  }

  public String getRequestValue()
  {
    return wikiContext.getRequestParameter(partName + "." + propDescriptor.getKey());
  }

  public String getPropsValue()
  {
    if (propsArtefakt.getCompiledObject() == null) {
      return "";
    }
    return StringUtils.defaultString(propsArtefakt.getCompiledObject().getStringValue(propDescriptor.getKey()));
  }

  public void setPropsValue(String value)
  {
    propsArtefakt.getCompiledObject().setStringValue(propDescriptor.getKey(), value);
  }

  public String getRequestKey()
  {
    return partName + "." + propDescriptor.getKey();
  }

  public String getControlType()
  {
    return propDescriptor.getType();
  }

  public boolean isReadOnly()
  {
    if (propDescriptor.isReadOnly() == true) {
      return true;
    }
    if (StringUtils.isBlank(propDescriptor.getRequiredEditRight()) == true) {
      return false;
    }
    if (wikiContext.isAllowTo(propDescriptor.getRequiredEditRight()) == false) {
      return true;
    }
    return false;
  }

  public boolean isDisplayable()
  {
    if (StringUtils.isBlank(propDescriptor.getRequiredEditRight()) == true) {
      return true;
    }
    if (wikiContext.isAllowTo(propDescriptor.getRequiredEditRight()) == false) {
      return false;
    }
    return true;
  }

  public String[] getRequestValues()
  {
    return wikiContext.getRequestValues(getRequestKey());
  }

  protected Object getControlerBean()
  {
    if (controlerBean != null) {
      return controlerBean;
    }
    if (StringUtils.isBlank(propDescriptor.getControlerScript()) == true) {
      return null;
    }
    controlerBean = ScriptUtils.getScriptObject(propDescriptor.getControlerScript());
    return controlerBean;
  }

  public String getDefaultValue()
  {
    if (propDescriptor.getDefaultValue() != null) {
      return propDescriptor.getDefaultValue();
    }
    if (ScriptUtils.hasMethod(getControlerBean(), "getDefaultValue", this) == true) {
      return (String) ScriptUtils.invokeScriptFunktion(getControlerBean(), "getDefaultValue", this);
    }
    return null;
  }

  public void setValue(String value)
  {
    propsArtefakt.getCompiledObject().setStringValue(getPropName(), value);
  }

  public PropsEditContext addSimpleValidationError(String message)
  {
    wikiContext.addSimpleValidationError(message);
    return this;
  }

  public String getEffectiveDerivedRight(String right)
  {
    // Object of = wikiContext.getRequestAttribute("form");
    String parentId = propsArtefakt.getCompiledObject().getStringValue(GWikiPropKeys.PARENTPAGE);
    if (StringUtils.isEmpty(parentId) == true) {
      return "NONE";
    }
    GWikiElementInfo pei = wikiContext.getWikiWeb().findElementInfo(parentId);
    if (pei == null) {
      if (GWikiPropKeys.AUTH_VIEW.equals(right) == true) {
        return GWikiAuthorizationRights.GWIKI_VIEWPAGES.name();
      } else if (GWikiPropKeys.AUTH_EDIT.equals(right) == true) {
        return GWikiAuthorizationRights.GWIKI_EDITPAGES.name();
      } else {
        return "UNKNOWN";
      }
    }
    String effr = wikiContext.getWikiWeb().getAuthorization().getEffectiveRight(wikiContext, pei, right);
    if (effr != null) {
      return effr;
    }
    return "UNKNOWN";
  }

  /**
   * 
   * @param Method
   * @param args
   * @return null if contoler method cannot be found.
   */
  public <T> T invokeControlerBean(String methodName, Object... args)
  {
    Object bean = getControlerBean();
    if (bean == null) {
      return null;
    }
    if (ScriptUtils.hasMethod(bean, methodName, args) == false) {
      return null;
    }
    return (T) ScriptUtils.invokeScriptFunktion(bean, methodName, args);
  }

  public boolean invokeOnControlerBean(String methodName)
  {
    Object bean = getControlerBean();
    if (bean == null) {
      return false;
    }
    if (ScriptUtils.hasMethod(bean, methodName, this) == false) {
      return false;
    }
    ScriptUtils.invokeScriptFunktion(bean, methodName, this);
    return true;
  }

  protected Map<String, String> getOptionValues()
  {
    Map<String, String> o = getPropDescriptor().getOptionValues();
    if (o != null) {
      return o;
    }
    o = invokeControlerBean("getOptionEntryMap", this);
    if (o == null) {
      return Collections.emptyMap();
    }
    return (Map<String, String>) o;
  }

  public PropsEditContext append(Object... data)
  {
    wikiContext.append(data);
    return this;
  }

  public PropsEditContext append(String data)
  {
    wikiContext.append(data);
    return this;
  }

  public GWikiContext getWikiContext()
  {
    return wikiContext;
  }

  public void setWikiContext(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

  public GWikiPropsDescriptorValue getPropDescriptor()
  {
    return propDescriptor;
  }

  public void setPropDescriptor(GWikiPropsDescriptorValue propDescriptor)
  {
    this.propDescriptor = propDescriptor;
  }

  public String getPropName()
  {
    return propDescriptor.getKey();
  }

}

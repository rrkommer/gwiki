/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.io.Serializable;
import java.util.Map;

public class GWikiPropsDescriptorValue implements Serializable
{
  private String key;

  /**
   * Label for display in editor. if not set uses key instead.
   */
  private String label;

  private String description;

  /*
   * <ul> <li>STRING: No restriction</li> <li>PAGEID</li> <li>PAGEIDLIST</li> <li>CLASSNAME</li> <li>BOOLEAN</li> <li>RIGHT</li>
   * <li>STRINGLIST</li> <li>TIMESTAMP</li> <li>OPTION</li>
   */
  private String type = "STRING";

  private boolean readOnly = false;

  private boolean requiresValue = false;

  private String requiredViewRight;

  private String requiredEditRight;

  /**
   * Wird nur dargestellt, wenn das Template verwendet wird.
   */
  private String requiredMetaTemplateId;

  /**
   * Default Value wird automatisch gesetzt.
   * 
   * if string value starts with "def getDefaultValue(" it will be evaluted a groovy method with signature:
   * 
   * String getDefaultValue(GWikiContext wikiContext, GWikiPropsDescriptorValue wikiPropDescriptor, String name);
   */
  private String defaultValue;

  // /**
  // * Groovy Code will be called on Validate. GWikiContext wikiContext GWikiElement wikiElement GWikiPropsDescriptorValue
  // wikiPropDescriptor
  // * String value;
  // *
  // * String onValidate(String value, GWikiContext wikiContext, GWikiElement wikiElement, GWikiPropsDescriptorValue wikiPropDescriptor) def
  // * onValidate(value, wikiContext, wikiElement, wikiPropDescriptor)
  // *
  // */
  // private String onValidate;

  /**
   * -> either a Map with key/values. key-> is internal name, value is used to display.
   * 
   * or a groovy function with following signature: Map getOptionCollection(GWikiContext wikiContext, GWikiPropsDescriptorValue
   * wikiPropDescriptor, String name, String value);
   */
  private Map<String, String> optionValues;

  private String controlerScript;

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly)
  {
    this.readOnly = readOnly;
  }

  public boolean isRequiresValue()
  {
    return requiresValue;
  }

  public void setRequiresValue(boolean requiresValue)
  {
    this.requiresValue = requiresValue;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
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

  public String getRequiredMetaTemplateId()
  {
    return requiredMetaTemplateId;
  }

  public void setRequiredMetaTemplateId(String requiredMetaTemplateId)
  {
    this.requiredMetaTemplateId = requiredMetaTemplateId;
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  public String getControlerScript()
  {
    return controlerScript;
  }

  public void setControlerScript(String controlerScript)
  {
    this.controlerScript = controlerScript;
  }

  public Map<String, String> getOptionValues()
  {
    return optionValues;
  }

  public void setOptionValues(Map<String, String> optionValues)
  {
    this.optionValues = optionValues;
  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

}

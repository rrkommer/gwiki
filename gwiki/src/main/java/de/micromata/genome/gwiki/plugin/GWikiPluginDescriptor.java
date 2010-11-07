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
package de.micromata.genome.gwiki.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Descriptor of a GWiki Plugin.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPluginDescriptor
{

  /**
   * Version of the plugin API.
   */
  private String apiVersion;

  /**
   * Name of plugin
   */
  private String name;

  /**
   * Version string in form mayor.minor.build
   */
  private String version;

  /**
   * Description
   */
  private String description;

  /**
   * List of element ids uses as public templates.
   */
  private List<String> templates = new ArrayList<String>();

  /**
   * registered macros. name -> Macro class.
   */
  private Map<String, String> macros = new HashMap<String, String>();

  /**
   * registered filter.
   */
  private List<GWikiPluginFilterDescriptor> filter = new ArrayList<GWikiPluginFilterDescriptor>();

  /**
   * registered element types.
   */
  private Map<String, String> elementTypes = new HashMap<String, String>();

  /**
   * text extractor classes. Key -> extension. Value class name implementing the de.micromata.genome.gwiki.page.attachments.TextExtractor
   * interface
   */
  private Map<String, String> textExtractors = new HashMap<String, String>();

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getVersion()
  {
    return version;
  }

  public void setVersion(String version)
  {
    this.version = version;
  }

  public String getApiVersion()
  {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion)
  {
    this.apiVersion = apiVersion;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public Map<String, String> getMacros()
  {
    return macros;
  }

  public void setMacros(Map<String, String> macros)
  {
    this.macros = macros;
  }

  public Map<String, String> getElementTypes()
  {
    return elementTypes;
  }

  public void setElementTypes(Map<String, String> elementTypes)
  {
    this.elementTypes = elementTypes;
  }

  public List<String> getTemplates()
  {
    return templates;
  }

  public void setTemplates(List<String> templates)
  {
    this.templates = templates;
  }

  public List<GWikiPluginFilterDescriptor> getFilter()
  {
    return filter;
  }

  public void setFilter(List<GWikiPluginFilterDescriptor> filter)
  {
    this.filter = filter;
  }

  public Map<String, String> getTextExtractors()
  {
    return textExtractors;
  }

  public void setTextExtractors(Map<String, String> textExtractors)
  {
    this.textExtractors = textExtractors;
  }

}

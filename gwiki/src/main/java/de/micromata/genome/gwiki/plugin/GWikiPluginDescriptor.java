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

package de.micromata.genome.gwiki.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import de.micromata.genome.gwiki.model.GWikiRight;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Descriptor of a GWiki Plugin.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPluginDescriptor implements InitializingBean
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
   * Alpha, Beta or Final
   */
  private String versionState;

  /**
   * Description
   */
  private String description;

  /**
   * Author
   */
  private String author;

  private String category;

  /**
   * Path to the logo to be displayed.
   */
  private String logoPath;

  /**
   * wiki internal page describing the plugin
   */
  private String descriptionPath;

  /**
   * Licence conditiono
   */
  private String license;

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
   * text extractor classes. Key -> extension. Value class name implementing the de.micromata.genome.gwiki.page.attachments.TextExtractor
   * interface
   */
  private Map<String, String> textExtractors = new HashMap<String, String>();

  /**
   * a class name with a lifecycle listener. Multiple classes may devided by ,.
   * 
   * Has to implement GWikiPluginLifecycleListener, normally derive from GWikiAbstractPluginLifecycleListener
   */
  private String pluginLifecycleListener = "";

  /**
   * List of required. One String contains: plugin_id:Version-Descriptor
   */
  private List<String> requiredPlugins = new ArrayList<String>();

  /**
   * Normally read operation are done in plugin. In some cases the file in the standard fs overwrites plugin files.
   */
  private String primaryFsReadMatcherRule = null;

  /**
   * will be initialized via primaryFsReadMatcherRule.
   */
  private Matcher<String> primaryFsReadMatcher = null;

  private List<GWikiRight> rights = new ArrayList<GWikiRight>();

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  public void afterPropertiesSet() throws Exception
  {
    if (StringUtils.isNotBlank(primaryFsReadMatcherRule) == true) {
      primaryFsReadMatcher = new BooleanListRulesFactory<String>().createMatcher(primaryFsReadMatcherRule);
    }
  }

  public String toString()
  {
    return getPluginId();
  }

  /**
   * 
   * @return name:version
   */
  public String getPluginId()
  {
    return name + ":" + version;
  }

  public boolean isPrimaryFsRead(String fname)
  {
    if (primaryFsReadMatcher == null) {
      return false;
    }
    return primaryFsReadMatcher.match(fname);
  }

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

  public String getPluginLifecycleListener()
  {
    return pluginLifecycleListener;
  }

  public void setPluginLifecycleListener(String pluginLifecycleListener)
  {
    this.pluginLifecycleListener = pluginLifecycleListener;
  }

  public List<String> getRequiredPlugins()
  {
    return requiredPlugins;
  }

  public void setRequiredPlugins(List<String> requiredPlugins)
  {
    this.requiredPlugins = requiredPlugins;
  }

  public String getPrimaryFsReadMatcherRule()
  {
    return primaryFsReadMatcherRule;
  }

  public void setPrimaryFsReadMatcherRule(String primaryFsReadMatcherRule)
  {
    this.primaryFsReadMatcherRule = primaryFsReadMatcherRule;
  }

  public Matcher<String> getPrimaryFsReadMatcher()
  {
    return primaryFsReadMatcher;
  }

  public void setPrimaryFsReadMatcher(Matcher<String> primaryFsReadMatcher)
  {
    this.primaryFsReadMatcher = primaryFsReadMatcher;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setLogoPath(String logoPath)
  {
    this.logoPath = logoPath;
  }

  public String getLogoPath()
  {
    return logoPath;
  }

  public void setCategory(String category)
  {
    this.category = category;
  }

  public String getCategory()
  {
    return category;
  }

  public List<GWikiRight> getRights()
  {
    return rights;
  }

  public void setRights(List<GWikiRight> rights)
  {
    this.rights = rights;
  }

  public String getLicense()
  {
    return license;
  }

  public void setLicense(String licence)
  {
    this.license = licence;
  }

  public String getDescriptionPath()
  {
    return descriptionPath;
  }

  public void setDescriptionPath(String descriptionPath)
  {
    this.descriptionPath = descriptionPath;
  }

  public String getVersionState()
  {
    return versionState;
  }

  public void setVersionState(String versionState)
  {
    this.versionState = versionState;
  }

}

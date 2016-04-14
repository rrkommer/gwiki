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

package de.micromata.genome.gwiki.model.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.plugin.GWikiPlugin;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPluginSpringBeanFactory extends AbstractFactoryBean
{
  private String className;

  private String pluginName;

  private String pluginVersion;

  private Class< ? > classClass;

  private Map<String, Object> beanProps = new HashMap<String, Object>();

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
   */
  @Override
  protected Object createInstance() throws Exception
  {
    resolveClass();
    Object ret = classClass.newInstance();
    if (beanProps.isEmpty() == false) {
      BeanUtils.populate(ret, beanProps);
    }
    return ret;
  }

  protected void resolveClass() throws Exception
  {
    if (classClass != null) {
      return;
    }
    GWikiWeb wikiWeb = GWikiWeb.get();

    List<GWikiPlugin> plugins = wikiWeb.getDaoContext().getPluginRepository().getActivePlugins();
    for (GWikiPlugin wp : plugins) {
      if (wp.isActivated() == false) {
        continue;
      }
      if (wp.getDescriptor().getName().equals(pluginName) == false) {
        continue;
      }
      classClass = wp.getPluginClassLoader().loadClass(className);
      return;
    }
    throw new RuntimeException("Plugin cannot be found or is not active: " + pluginName);

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.config.AbstractFactoryBean#getObjectType()
   */
  @Override
  public Class< ? > getObjectType()
  {
    try {
      resolveClass();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return classClass;
  }

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public String getPluginName()
  {
    return pluginName;
  }

  public void setPluginName(String pluginName)
  {
    this.pluginName = pluginName;
  }

  public String getPluginVersion()
  {
    return pluginVersion;
  }

  public void setPluginVersion(String pluginVersion)
  {
    this.pluginVersion = pluginVersion;
  }

  public Class< ? > getClassClass()
  {
    return classClass;
  }

  public void setClassClass(Class< ? > classClass)
  {
    this.classClass = classClass;
  }

  public Map<String, Object> getBeanProps()
  {
    return beanProps;
  }

  public void setBeanProps(Map<String, Object> beanProps)
  {
    this.beanProps = beanProps;
  }
}

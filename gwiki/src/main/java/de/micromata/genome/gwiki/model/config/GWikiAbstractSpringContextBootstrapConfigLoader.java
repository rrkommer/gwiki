////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

package de.micromata.genome.gwiki.model.config;

import javax.servlet.ServletConfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Base implementation to load GWikiDAOContext from a spring context file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiAbstractSpringContextBootstrapConfigLoader implements GWikiBootstrapConfigLoader
{
  protected String fileName;

  /**
   * 
   * @param config may null if not loaded in a servlet container.
   * @param fileName
   * @return
   */
  protected abstract ConfigurableApplicationContext createApplicationContext(ServletConfig config, String fileName);

  protected BeanFactory beanFactory;

  protected boolean supportsJndi = true;

  public String getApplicationContextName()
  {
    if (StringUtils.isEmpty(fileName) == true) {

      fileName = "GWikiContext.xml";
    }
    return fileName;
  }

  public GWikiDAOContext loadConfig(ServletConfig config)
  {
    if (config != null) {
      fileName = config.getInitParameter("de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader.fileName");
      supportsJndi = StringUtils.equals(config.getInitParameter("de.micromata.genome.gwiki.supportsJndi"), "true");
    }
    ConfigurableApplicationContext actx = createApplicationContext(config, getApplicationContextName());
    actx.addBeanFactoryPostProcessor(new GWikiDAOContextPropertyPlaceholderConfigurer(config, supportsJndi));
    actx.refresh();
    beanFactory = actx;
    GWikiDAOContext daoContext = (GWikiDAOContext) actx.getBean("GWikiBootstrapConfig");

    daoContext.setBeanFactory(beanFactory);
    return daoContext;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public BeanFactory getBeanFactory()
  {
    return beanFactory;
  }

  public void setBeanFactory(BeanFactory beanFactory)
  {
    this.beanFactory = beanFactory;
  }
}

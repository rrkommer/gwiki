/////////////////////////////////////////////////////////////////////////////
//
// Project   genome-gwiki-standalone
//
// Author    roger@micromata.de
// Created   06.11.2009
// Copyright Micromata 06.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.config;

import javax.servlet.ServletConfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class GwikiFileContextBootstrapConfigLoader implements GWikiBootstrapConfigLoader
{
  private String fileName;

  private BeanFactory beanFactory;

  protected void initBeanFactory()
  {
    if (beanFactory != null) {
      return;
    }

    if (StringUtils.isEmpty(fileName) == true) {

      fileName = "GWikiContext.xml";
    }
    Resource res = new FileSystemResource(fileName);
    beanFactory = new XmlBeanFactory(res);
  }

  public GWikiDAOContext loadConfig(ServletConfig config)
  {
    if (config != null) {
      fileName = config.getInitParameter("de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader.fileName");
    }
    initBeanFactory();
    return (GWikiDAOContext) beanFactory.getBean("GWikiBootstrapConfig");
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
    initBeanFactory();
    return beanFactory;
  }

  public void setBeanFactory(BeanFactory beanFactory)
  {
    this.beanFactory = beanFactory;
  }
}

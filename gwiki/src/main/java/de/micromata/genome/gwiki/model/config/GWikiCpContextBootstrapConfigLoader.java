/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.11.2009
// Copyright Micromata 02.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.config;

import javax.servlet.ServletConfig;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Looks for GWikiContext.xml in the class laoder.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiCpContextBootstrapConfigLoader implements GWikiBootstrapConfigLoader
{

  public GWikiDAOContext loadConfig(ServletConfig config)
  {
    Resource res = new ClassPathResource("GWikiContext.xml");
    BeanFactory bf = new XmlBeanFactory(res);
    return (GWikiDAOContext) bf.getBean("GWikiBootstrapConfig");
  }
}

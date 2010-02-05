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

import org.springframework.context.support.ClassPathXmlApplicationContext;

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
    String xmlFile = "GWikiContext.xml";
    //Resource res = new ClassPathResource("GWikiContext.xml");

    ClassPathXmlApplicationContext cpt = new ClassPathXmlApplicationContext(xmlFile);
    cpt.addBeanFactoryPostProcessor(new GWikiDAOContextPropertyPlaceholderConfigurer(config));
    cpt.refresh();
    // BeanFactory bf = new XmlBeanFactory(res);
    return (GWikiDAOContext) cpt.getBean("GWikiBootstrapConfig");
  }
}

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

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Looks for GWikiContext.xml in the class laoder.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiCpContextBootstrapConfigLoader extends GWikiAbstractSpringContextBootstrapConfigLoader
{

  @Override
  protected ConfigurableApplicationContext createApplicationContext(String fileName)
  {
    return new ClassPathXmlApplicationContext(fileName);
  }
}

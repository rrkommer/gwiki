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

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class GwikiFileContextBootstrapConfigLoader extends GWikiAbstractSpringContextBootstrapConfigLoader
{

  @Override
  protected ConfigurableApplicationContext createApplicationContext(String fileName)
  {
    return new FileSystemXmlApplicationContext(new String[] { fileName}, false, null);
  }
}

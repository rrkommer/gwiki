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

/**
 * Loads the bootstrap configuration for the wiki.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiBootstrapConfigLoader
{
  GWikiDAOContext loadConfig(ServletConfig config);

}

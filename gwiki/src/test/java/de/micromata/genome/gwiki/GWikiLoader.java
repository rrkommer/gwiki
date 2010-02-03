/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.11.2009
// Copyright Micromata 21.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiCpContextBootstrapConfigLoader;

public class GWikiLoader
{
  public static GWikiWeb loadGWikiWeb()
  {
    GWikiBootstrapConfigLoader loader = new GWikiCpContextBootstrapConfigLoader();
    GWikiDAOContext bootStrapConfig = loader.loadConfig(null);
    GWikiWeb web = new GWikiWeb(bootStrapConfig);
    return web;
  }
}

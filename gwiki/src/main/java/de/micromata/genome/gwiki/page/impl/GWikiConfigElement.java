/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.util.Map;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAbstractElement;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiXmlConfigArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * XML file configuration item.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiConfigElement extends GWikiAbstractElement
{

  private static final long serialVersionUID = -3877389856566990574L;

  private GWikiXmlConfigArtefakt< ? > config = new GWikiXmlConfigArtefakt();

  public GWikiConfigElement(GWikiElementInfo elementInfo)
  {
    super(elementInfo);
  }

  public void collectParts(Map<String, GWikiArtefakt< ? >> map)
  {
    map.put("", config);
    super.collectParts(map);
    

  }

  public GWikiArtefakt<?> getMainPart()
  {
    return config;
  }

  public void serve(GWikiContext ctx)
  {
    AuthorizationFailedException.failRight(ctx, "INVALID_PAGE");
  }

  public GWikiXmlConfigArtefakt<?> getConfig()
  {
    return config;
  }

  public void setConfig(GWikiXmlConfigArtefakt<?> config)
  {
    this.config = config;
  }

}

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
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.test.web.SimHttpServletRequest;
import de.micromata.genome.test.web.SimHttpServletResponse;

public class GWikiMockupContext extends GWikiContext
{

  public GWikiMockupContext(GWikiWeb wikiWeb)
  {
    super(wikiWeb, null, new SimHttpServletRequest(), new SimHttpServletResponse());
  }

}

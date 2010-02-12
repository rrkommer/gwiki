/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   23.10.2009
// Copyright Micromata 23.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.web.tags;

import javax.servlet.jsp.tagext.TagSupport;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Base implementation for GWiki Tags.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class WikiSimpleTagBase extends TagSupport
{

  private static final long serialVersionUID = -7658522982913202856L;

  protected GWikiContext getWikiContext()
  {
    return (GWikiContext) pageContext.getAttribute("wikiContext");
  }
}

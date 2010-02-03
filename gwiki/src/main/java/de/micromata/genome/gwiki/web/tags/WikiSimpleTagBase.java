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

public abstract class WikiSimpleTagBase extends TagSupport
{
  protected GWikiContext getWikiContext()
  {
    return (GWikiContext) pageContext.getAttribute("wikiContext");
  }
}

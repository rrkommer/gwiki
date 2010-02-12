/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.11.2009
// Copyright Micromata 06.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.io.Serializable;

import javax.servlet.jsp.PageContext;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author roger
 * 
 */
public interface GWikiJspProcessor
{
  Serializable compile(GWikiContext ctx, String text);

  /**
   * 
   * @param ctx
   * @param template compiled template
   */
  void renderTemplate(GWikiContext ctx, Object template);

  PageContext createPageContext(GWikiContext ctx);
}

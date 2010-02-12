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

import groovy.text.Template;

import java.io.Serializable;

import javax.servlet.jsp.PageContext;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GenomeJspProcessor implements GWikiJspProcessor
{
  protected void initPageContext(GWikiContext ctx)
  {
    if (ctx.getPageContext() == null) {
      PageContext pageContext = GenomeTemplateUtils.initPageContext(ctx);
      ctx.setPageContext(pageContext);
    }
  }

  public Serializable compile(GWikiContext ctx, String text)
  {
    initPageContext(ctx);
    Template ct = GenomeTemplateUtils.compile(ctx, text);
    return new TemplateHolder(ct);
  }

  public void renderTemplate(GWikiContext ctx, Object template)
  {
    initPageContext(ctx);
    TemplateHolder th = (TemplateHolder) template;
    Template ct = th.getTemplate();
    GenomeTemplateUtils.processPage(ct, ctx, null);
  }

  public PageContext createPageContext(GWikiContext ctx)
  {
    return GenomeTemplateUtils.initPageContext(ctx);
  }

}

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

import java.io.IOException;

import javax.servlet.jsp.JspException;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Generates a wiki link.
 * 
 * @author roger
 * 
 */
public class WikiLinkTag extends WikiSimpleTagBase
{

  private static final long serialVersionUID = 6695603589254623511L;

  private String pageId;

  private String urlParams;

  private String title;

  @Override
  public int doEndTag() throws JspException
  {
    // JspContext jc = getJspContext();
    final GWikiContext wctx = getWikiContext();
    String url = wctx.renderLocalUrl(pageId, title, urlParams);
    try {
      pageContext.getOut().print(url);
    } catch (IOException ex) {
      throw new JspException(ex);
    }
    return EVAL_PAGE;
  }

  // /**
  // * Does nothing.
  // *
  // * @return {@link #EVAL_BODY_BUFFERED} in all cases.
  // */
  // public int doStartTag() throws JspException
  // {
  // return EVAL_BODY_BUFFERED;
  // }

  /** Does nothing. */
  public void doInitBody() throws JspException
  { /* Do Nothing. */
  }

  /**
   * Does nothing.
   * 
   * @return {@link #SKIP_BODY} in all cases.
   */
  public int doAfterBody() throws JspException
  {
    return SKIP_BODY;
  }

  // //@Override
  // public void doTag() throws JspException, IOException
  // {
  //    
  // }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getUrlParams()
  {
    return urlParams;
  }

  public void setUrlParams(String urlParams)
  {
    this.urlParams = urlParams;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

}

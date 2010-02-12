/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Tag for authentification.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiAuthTag extends TagSupport
{
  // ~ Instance fields ================================================================================================

  /**
   * 
   */
  private static final long serialVersionUID = -52584218565083852L;

  private String ifHasAllOf = "";

  private String ifHasAny = "";

  private String ifHasNot = "";

  private String pageId;

  private static final String RIGHTS_SEPARATOR = ",";

  // ~ Methods ========================================================================================================
  protected GWikiContext getWikiContext()
  {
    return (GWikiContext) pageContext.getAttribute("wikiContext");
  }

  @Override
  public int doStartTag() throws JspException
  {
    GWikiContext ctx = getWikiContext();
    GWikiAuthorization auth = ctx.getWikiWeb().getAuthorization();

    if (StringUtils.isNotBlank(pageId) == true) {
      GWikiElementInfo pid = ctx.getWikiWeb().findElementInfo(pageId);
      if (pid == null || auth.isAllowToView(ctx, pid) == false) {
        return Tag.SKIP_BODY;
      }
      return Tag.EVAL_BODY_INCLUDE;
    }
    GWikiElementInfo ei = ctx.getWikiElement() != null ? ctx.getWikiElement().getElementInfo() : null;
    /* The User must not have any if rights */
    if (StringUtils.isNotBlank(ifHasNot) == true) {
      for (String right : splitRights(ifHasNot)) {
        if (auth.isAllowTo(ctx, StringUtils.trim(right))) {
          return Tag.SKIP_BODY;
        }
      }
      return Tag.EVAL_BODY_INCLUDE;
    }

    /* The user must have all rights */
    if (StringUtils.isNotBlank(ifHasAllOf)) {

      boolean hasAll = true;
      for (String right : splitRights(ifHasAllOf)) {
        hasAll &= auth.isAllowTo(ctx, StringUtils.trim(right));
      }
      if (hasAll) {
        return Tag.EVAL_BODY_INCLUDE;
      }
      return Tag.SKIP_BODY;
    }

    if (StringUtils.isNotBlank(ifHasAny)) {

      for (String rigth : splitRights(ifHasAny)) {
        if (auth.isAllowTo(ctx, StringUtils.trim(rigth))) {
          return Tag.EVAL_BODY_INCLUDE;
        }
      }
      return Tag.SKIP_BODY;
    }
    return Tag.EVAL_BODY_INCLUDE;
  }

  @Override
  public void release()
  {
    super.release();
    ifHasAllOf = null;
    ifHasAny = null;
    ifHasNot = null;
  }

  private String[] splitRights(String rightsString)
  {
    return StringUtils.split(rightsString, RIGHTS_SEPARATOR);
  }

  public String getIfHasAllOf()
  {
    return ifHasAllOf;
  }

  public void setIfHasAllOf(String ifHasAllOf)
  {
    this.ifHasAllOf = ifHasAllOf;
  }

  public String getIfHasAny()
  {
    return ifHasAny;
  }

  public void setIfHasAny(String ifHasAny)
  {
    this.ifHasAny = ifHasAny;
  }

  public String getIfHasNot()
  {
    return ifHasNot;
  }

  public void setIfHasNot(String ifHasNot)
  {
    this.ifHasNot = ifHasNot;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }
}

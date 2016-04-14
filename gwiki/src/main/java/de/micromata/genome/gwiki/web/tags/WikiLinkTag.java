//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Generates a wiki link.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class WikiLinkTag extends WikiSimpleTagBase
{

  private static final long serialVersionUID = 6695603589254623511L;

  private String pageId;

  private String urlParams;

  /**
   * direkt title
   */
  private String title;

  /**
   * i18n key for title
   */
  private String titleKey;

  @Override
  public int doEndTag() throws JspException
  {
    // JspContext jc = getJspContext();
    final GWikiContext wctx = getWikiContext();
    if (StringUtils.isNotEmpty(titleKey) == true) {
      title = wctx.getWikiWeb().getI18nProvider().translate(wctx, titleKey);
    }
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

  public String getTitleKey()
  {
    return titleKey;
  }

  public void setTitleKey(String titleKey)
  {
    this.titleKey = titleKey;
  }

}

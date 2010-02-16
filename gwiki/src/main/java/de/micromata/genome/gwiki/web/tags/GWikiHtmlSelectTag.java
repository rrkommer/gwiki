////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;

import org.apache.commons.lang.StringUtils;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlSelectTag extends GWikiBasePropertyHtmlTag
{

  private static final long serialVersionUID = -364949859183919421L;

  public static final String GWikiHtmlSelectTag_KEY = GWikiHtmlSelectTag.class.getName() + "_KEY";

  protected String multiple = null;

  @ElementProperty
  protected String value = null;

  @ElementProperty
  protected String size;

  protected String saveBody;

  public boolean hasValue(String value)
  {
    return StringUtils.equals(this.value, value);
  }

  @Override
  public void prepare()
  {
    if (value == null) {
      value = GWikiTagRenderUtils.readFormStringValue(pageContext, property);
    }
  }

  public int doStartTag() throws JspException
  {
    prepare();
    StringBuilder sb = new StringBuilder();
    GWikiTagRenderUtils.renderSimpleHtmlTag(this, "select", sb);
    GWikiTagRenderUtils.write(pageContext, sb.toString());
    pageContext.setAttribute(GWikiHtmlSelectTag_KEY, this);
    return BodyTag.EVAL_BODY_BUFFERED;
  }

  public int doAfterBody() throws JspException
  {
    if (bodyContent != null) {
      String value = bodyContent.getString();
      if (value == null) {
        value = "";
      }
      this.saveBody = value.trim();
    }
    return SKIP_BODY;
  }

  /**
   * Render the end of this form.
   * 
   * @exception JspException if a JSP exception has occurred
   */
  public int doEndTag() throws JspException
  {
    pageContext.removeAttribute(GWikiHtmlSelectTag_KEY);
    StringBuilder sb = new StringBuilder();
    if (saveBody != null) {
      sb.append(saveBody);
      saveBody = null;
    }
    sb.append("</select>");
    GWikiTagRenderUtils.write(pageContext, sb.toString());
    return EVAL_PAGE;
  }

  public String getMultiple()
  {
    return multiple;
  }

  public void setMultiple(String multiple)
  {
    this.multiple = multiple;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getSize()
  {
    return size;
  }

  public void setSize(String size)
  {
    this.size = size;
  }
}

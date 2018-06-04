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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;

import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlTextareaTag extends GWikiBasePropertyHtmlTag
{

  private static final long serialVersionUID = 6519056127184071681L;

  @ElementProperty
  protected String value;

  @ElementProperty
  protected String cols;

  @ElementProperty
  protected String rows;

  @Override
  public void prepare()
  {

  }

  @Override
  public int doStartTag() throws JspException
  {
    prepare();
    StringBuilder sb = new StringBuilder();
    GWikiTagRenderUtils.renderOpenHtmlTag(this, "textarea", sb);
    GWikiTagRenderUtils.write(pageContext, sb.toString());
    GWikiTagRenderUtils.write(pageContext, WebUtils.escapeHtml(StringUtils.defaultString(value)));
    GWikiTagRenderUtils.write(pageContext, "</textarea>");
    return BodyTag.EVAL_BODY_BUFFERED;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getCols()
  {
    return cols;
  }

  public void setCols(String cols)
  {
    this.cols = cols;
  }

  public String getRows()
  {
    return rows;
  }

  public void setRows(String rows)
  {
    this.rows = rows;
  }

}

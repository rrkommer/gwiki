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

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlCheckboxTag extends GWikiBasePropertyHtmlTag
{

  private static final long serialVersionUID = -335595137230600687L;

  protected String value;

  @ElementProperty
  protected String checked;

  @ElementProperty
  protected String type = "checkbox";

  @Override
  public void prepare()
  {
    if (value == null) {
      value = "on";
    }
    String sval = "";
    Object val = GWikiTagRenderUtils.readFormValue(pageContext, property);
    if (val == null) {
      val = "";
    } else {
      sval = val.toString();
    }
    if (sval.equalsIgnoreCase(this.value) || sval.equalsIgnoreCase("true") || sval.equalsIgnoreCase("yes") || sval.equalsIgnoreCase("on")) {
      checked = "checked";
    }
  }

  public int doStartTag() throws JspException
  {
    prepare();
    StringBuilder sb = new StringBuilder();
    GWikiTagRenderUtils.renderSimpleHtmlTag(this, "input", sb);
    GWikiTagRenderUtils.write(pageContext, sb.toString());
    return BodyTag.EVAL_BODY_BUFFERED;
  }

  public String getChecked()
  {
    return checked;
  }

  public void setChecked(String checked)
  {
    this.checked = checked;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

}

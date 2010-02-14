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
public abstract class GWikiBaseFieldInputTag extends GWikiBasePropertyHtmlTag
{

  private static final long serialVersionUID = -6072810126435299135L;

  @ElementProperty
  protected String type;

  @ElementProperty
  protected String value;

  @TagProperty
  @ElementProperty
  protected String size;

  public GWikiBaseFieldInputTag(String type)
  {
    this.type = type;
  }

  @Override
  public void prepare()
  {
    if (value == null) {
      value = GWikiTagRenderUtils.readFormValue(pageContext, property);
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

  public String getProperty()
  {
    return property;
  }

  public void setProperty(String property)
  {
    this.property = property;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
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

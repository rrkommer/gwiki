////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

/**
 * <html:hidden parameter="name"/>
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlHiddenTag extends GWikiBaseFieldInputTag
{

  private static final long serialVersionUID = -5284813398719407445L;

  public GWikiHtmlHiddenTag()
  {
    super("hidden");
  }
  //  
  // public int doStartTag() throws JspException
  // {
  // prepare();
  // StringBuilder sb = new StringBuilder();
  // GWikiTagRenderUtils.renderSimpleHtmlTag(this, "input", sb);
  // GWikiTagRenderUtils.write(pageContext, sb.toString());
  // return BodyTag.EVAL_BODY_BUFFERED;
  // }

  // public String getType()
  // {
  // return type;
  // }
  //
  // public void setType(String type)
  // {
  // this.type = type;
  // }

}

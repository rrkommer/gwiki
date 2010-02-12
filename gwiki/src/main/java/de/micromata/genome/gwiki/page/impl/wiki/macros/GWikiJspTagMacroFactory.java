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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.TagInfo;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.utils.ClassUtils;

/**
 * Factory for a GWikiJspTagMacro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiJspTagMacroFactory implements GWikiMacroFactory
{
  private TagInfo tagInfo;

  private JspTag tag;

  public GWikiJspTagMacroFactory(TagInfo tagInfo)
  {
    super();
    this.tagInfo = tagInfo;
    this.tag = createTag();
  }

  public String toString()
  {
    return "JspTag(" + tagInfo.getTagName() + ")";
  }

  protected JspTag createTag()
  {
    String className = tagInfo.getTagClassName();
    return ClassUtils.createDefaultInstance(className, JspTag.class);
  }

  public GWikiMacro createInstance()
  {
    return new GWikiJspTagMacro(tagInfo, createTag());
  }

  public boolean evalBody()
  {
    return tag instanceof BodyTag;
    // return false;
  }

  public boolean hasBody()
  {
    return evalBody();
  }

  public boolean isRteMacro()
  {
    return false;
  }
}

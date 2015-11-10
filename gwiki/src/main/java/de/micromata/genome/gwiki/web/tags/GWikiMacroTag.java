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

import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.parser.WikiParserUtils;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMacroTag extends BodyTagSupport
{

  private static final long serialVersionUID = 2820272406494324767L;

  /**
   * name of the macro
   */
  private String name;

  /**
   * Macro args as pipe value list
   * 
   */
  private String args;

  private String saveBody;

  protected GWikiContext getWikiContext()
  {
    return (GWikiContext) pageContext.getAttribute("wikiContext");
  }

  @Override
  public int doStartTag() throws JspException
  {
    return 3; // no idea3 is just not used in Tag., Tag.EVAL_BODY_INCLUDE will not set body content;
  }

  public int doEndTag() throws JspException
  {
    GWikiContext wikiContext = getWikiContext();
    Map<String, GWikiMacroFactory> mfm = wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext);
    GWikiMacroFactory fac = mfm.get(name);
    if (fac == null) {
      GWikiTagRenderUtils.write(pageContext, wikiContext.getTranslated("gwiki.web.tags.macrotag.unknown") + " " + name);
      return Tag.EVAL_PAGE;
    }
    GWikiMacro macro = fac.createInstance();
    if ((macro instanceof GWikiRuntimeMacro) == false) {
      GWikiTagRenderUtils.write(pageContext, wikiContext.getTranslated("gwiki.web.tags.macrotag.runtime") + " " + name);
      return Tag.EVAL_PAGE;
    }
    String t = name;
    if (StringUtils.isNotBlank(args) == true) {
      t = name + ":" + args;
    }
    MacroAttributes attributes = new MacroAttributes(t);
    GWikiRuntimeMacro rtm = (GWikiRuntimeMacro) macro;
    if (macro.hasBody() == true && StringUtils.isNotEmpty(saveBody) == true) {
      attributes.setChildFragment(new GWikiFragmentChildContainer(WikiParserUtils.parseText(saveBody, mfm)));
    } else {
      attributes.setChildFragment(new GWikiFragmentChildContainer());
    }
    rtm.render(attributes, wikiContext);
    return Tag.EVAL_PAGE;
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

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getArgs()
  {
    return args;
  }

  public void setArgs(String args)
  {
    this.args = args;
  }

  public String getSaveBody()
  {
    return saveBody;
  }

  public void setSaveBody(String saveBody)
  {
    this.saveBody = saveBody;
  }
}

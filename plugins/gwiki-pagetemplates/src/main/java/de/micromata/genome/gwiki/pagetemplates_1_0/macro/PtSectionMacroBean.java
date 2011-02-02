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
package de.micromata.genome.gwiki.pagetemplates_1_0.macro;

import java.net.URLEncoder;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Defines an editable section.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 */
public class PtSectionMacroBean extends GWikiMacroBean implements GWikiBodyEvalMacro
{

  private static final long serialVersionUID = -3101167204944678243L;

  /**
   * Name of the section
   */
  private String name;

  /**
   * type of the editor.
   */
  private String editor;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    GWikiElementInfo ei = ctx.getWikiWeb().findElementInfo("edit/PageSectionEditor");
    if (ctx.getCurrentElement() != null
        && ctx.getWikiWeb().getAuthorization().isAllowToEdit(ctx, ctx.getCurrentElement().getElementInfo()) == true) {
     
      
      if (ei != null) {
        ctx.append("<div onmouseover=\"this.style.border = '1px dashed'\" onmouseout=\"this.style.border = '0px'\"");
        
        String link = ctx.renderExistingLink(ei, "(E)", "?pageId="
            + URLEncoder.encode(ctx.getCurrentElement().getElementInfo().getId())
            + "&sectionName="
            + URLEncoder.encode(name)
            + (editor == null ? "" : ("&editor=" + URLEncoder.encode(editor))));
        
        ctx.append(link);
      }
    }
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    if (ei != null) {
      ctx.append("</div>");
    }
    return true;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getEditor()
  {
    return editor;
  }

  public void setEditor(String editor)
  {
    this.editor = editor;
  }
}

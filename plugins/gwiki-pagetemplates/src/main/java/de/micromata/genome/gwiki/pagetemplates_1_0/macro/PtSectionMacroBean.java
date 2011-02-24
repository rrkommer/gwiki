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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiRawTextEditor;

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
  
  /**
   * optional hint, displays in a html-paragraph before the editor appears
   */
  private String hint;
  
  /**
   * optional field for images to limit the width of an uploaded image
   */
  private String maxWidth;
  
  /**
   * optional field, that allows to render wiki elements in the {@link PtWikiRawTextEditor}
   */
  private boolean allowWikiSyntax = false;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    GWikiElementInfo ei = ctx.getWikiWeb().findElementInfo("edit/pagetemplates/PageSectionEditor");
    if (ctx.getCurrentElement() != null
        && ctx.getWikiWeb().getAuthorization().isAllowToEdit(ctx, ctx.getCurrentElement().getElementInfo()) == true) {
      
      if (ei != null) {
        ctx.getWikiWeb().getI18nProvider().addTranslationElement(ctx, "edit/pagetemplates/i18n/PtI18N");

        final String edit = ctx.getWikiWeb().getI18nProvider().translate(ctx, "gwiki.pt.common.edit");
        final String editImage = "<img src='/inc/gwiki/img/icons/linedpaperpencil32.png' style='position:absolute; right: 0; margin-right:-20px' border=0/>";

        ctx.append("<div style=\"position:relative; padding: 1px\" onmouseover=\"this.style.border = '1px dashed'; this.style.padding = '0px'\" onmouseout=\"this.style.border = '0px'; this.style.padding='1px'\"");
        
        boolean allowed = ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, ei);

        if (allowed) {
          try {
            String id = ei.getId();
            String url = ctx.localUrl("/" + id)
                + "?pageId="
                + URLEncoder.encode(ctx.getCurrentElement().getElementInfo().getId(), "UTF-8")
                + "&sectionName="
                + URLEncoder.encode(name, "UTF-8")
                + (editor == null ? "" : ("&editor=" + URLEncoder.encode(editor, "UTF-8")))
                + (hint == null ? "" : ("&hint=" + URLEncoder.encode(hint, "UTF-8")))
                + (allowWikiSyntax ? ("&allowWikiSyntax=" + URLEncoder.encode(allowWikiSyntax + "", "UTF-8")) : "")
                + (maxWidth == null ? "" : ("&maxWidth=" + URLEncoder.encode(maxWidth, "UTF-8")));

            ctx.append("<a title=\"" + edit + "\" href=\"" + url + "\">" + editImage + "</a>");
            
            } catch (UnsupportedEncodingException ex) {
              GWikiLog.warn("Error rendering section edit link");
            }
        } 
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

  /**
   * @param hint the hint to set
   */
  public void setHint(String hint)
  {
    this.hint = hint;
  }

  /**
   * @return the hint
   */
  public String getHint()
  {
    return hint;
  }

  /**
   * @param allowWikiSyntax the allowWikiSyntax to set
   */
  public void setAllowWikiSyntax(boolean allowWikiSyntax)
  {
    this.allowWikiSyntax = allowWikiSyntax;
  }

  /**
   * @return the allowWikiSyntax
   */
  public boolean isAllowWikiSyntax()
  {
    return allowWikiSyntax;
  }

  /**
   * @param maxWidth the maxWidth to set
   */
  public void setMaxWidth(String maxWidth)
  {
    this.maxWidth = maxWidth;
  }

  /**
   * @return the maxWidth
   */
  public String getMaxWidth()
  {
    return maxWidth;
  }
}

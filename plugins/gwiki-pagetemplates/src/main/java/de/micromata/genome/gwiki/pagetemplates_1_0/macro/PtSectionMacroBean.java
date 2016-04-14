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

package de.micromata.genome.gwiki.pagetemplates_1_0.macro;

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.a;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.table;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiRawTextEditor;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * Defines an editable section.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * @author Christian Claus (c.claus@micromata.de)
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
   * optional field for attachments or images to limit the physical size
   */
  private String maxFileSize;

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
  public boolean renderImpl(final GWikiContext ctx, MacroAttributes attrs)
  {
    GWikiElementInfo ei = ctx.getWikiWeb().findElementInfo("edit/pagetemplates/PageSectionEditor");
    if (ctx.getCurrentElement() != null) {

      if (ei != null) {
        ctx.getWikiWeb().getI18nProvider().addTranslationElement(ctx, "edit/pagetemplates/i18n/PtI18N");

        final String edit = ctx.getWikiWeb().getI18nProvider().translate(ctx, "gwiki.pt.common.edit");
        final String path = ctx.getWikiWeb().getContextPath() + ctx.getWikiWeb().getServletPath();
        
        final String image = "<img src='" + path + "/inc/gwiki/img/icons/linedpaperpencil32.png' style='position:absolute; right: 0; margin-right:-20px' border=0/>";

        GWikiAuthorization authorization = ctx.getWikiWeb().getAuthorization();
        GWikiElementInfo currElementInfo = ctx.getCurrentElement().getElementInfo();

        if (authorization.isAllowToView(ctx, currElementInfo) && authorization.isAllowToEdit(ctx, currElementInfo)) {
          ctx.append("<div style=\"position:relative; padding: 1px\" onmouseover=\"this.style.border = '1px dashed'; this.style.padding = '0px'\" onmouseout=\"this.style.border = '0px'; this.style.padding='1px'\">");

          try {
            String id = ei.getId();
            String url = getUrl(ctx, id, ctx.getCurrentElement().getElementInfo().getId());

            ctx.append("<a id=\"" + URLEncoder.encode(name, "UTF-8") + "\" title=\"" + edit + "\" href=\"" + url + "\">" + image + "</a>");

            renderFancyBox(ctx, name);

          } catch (UnsupportedEncodingException ex) {
            GWikiLog.warn("Error rendering section edit link");
          }
        }

        // render simple div if no edit rights
        else {
          ctx.append("<div>");
        }

      }
    }

    if (attrs.getChildFragment() != null) {
      if ("attachment".equals(editor) || "link".equals(editor)) {
        List<GWikiFragment> childs = attrs.getChildFragment().getChilds();

        XmlElement ta = table(attrs("border", "0", "cellspacing", "0", "cellpadding", "2"));

        int i = 0;
        for (GWikiFragment child : childs) {
          if (child instanceof GWikiFragmentLink) {
            GWikiFragmentLink attachment = (GWikiFragmentLink) child;

            String target = GWikiFragmentLink.isGlobalUrl(attachment.getTarget()) ? attachment.getTarget() : ctx.localUrl(attachment
                .getTargetPageId());
            String title = (attachment.getTitle() != null) ? attachment.getTitle() : "";

            XmlElement targetLink = a(attrs("href", target), text(title));

            ta.nest(//
            tr(//
            td(attrs("width", "180px"), targetLink)) //
            );

            try {
              renderFancyBox(ctx, name + i);
            } catch (UnsupportedEncodingException ex) {
              GWikiLog.warn("", ex);
            }

            i++;
          }
        }

        if (ta.getChilds().size() > 0) {
          ctx.append(ta.toString());
        }

      } else {
        attrs.getChildFragment().render(ctx);
      }
    }

    if (ei != null) {
      ctx.append("</div>");
    }

    return true;
  }

  /**
   * @param ctx
   * @param id
   * @return
   * @throws UnsupportedEncodingException
   */
  private String getUrl(final GWikiContext ctx, String id, String parentElem) throws UnsupportedEncodingException
  {
    String url = ctx.localUrl("/" + id)
        + "?pageId="
        + URLEncoder.encode(parentElem, "UTF-8")
        + "&sectionName="
        + URLEncoder.encode(name, "UTF-8")
        + (editor == null ? "" : ("&editor=" + URLEncoder.encode(editor, "UTF-8")))
        + (hint == null ? "" : ("&hint=" + URLEncoder.encode(hint, "UTF-8")))
        + (allowWikiSyntax ? ("&allowWikiSyntax=" + URLEncoder.encode(allowWikiSyntax + "", "UTF-8")) : "")
        + (maxWidth == null ? "" : ("&maxWidth=" + URLEncoder.encode(maxWidth, "UTF-8")))
        + (maxFileSize == null ? "" : ("&maxFileSize=" + URLEncoder.encode(maxFileSize, "UTF-8")));
    return url;
  }

  /**
   * @param ctx
   * @throws UnsupportedEncodingException
   */
  private void renderFancyBox(final GWikiContext ctx, String id) throws UnsupportedEncodingException
  {
    int width = 1100;
    int height = 650;

    if ("link".equals(editor)) {
      width = 700;
      height = 600;
    } else if ("image".equals(editor)) {
      width = 700;
    } else if ("attachment".equals(editor)) {
      width = 700;
      height = 400;
    }

    ctx.append("\n<script type=\"text/javascript\">\njQuery(document).ready(function() {\n"
        + "$(\"#"
        + URLEncoder.encode(id, "UTF-8")
        + "\").fancybox({\n"
        + "width: "
        + width
        + ",\n"
        + "height: "
        + height
        + ",\n"
        + "type: 'iframe'\n"
        + "});\n"
        + "});\n"
        + "</script>\n");
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

  public void setHint(String hint)
  {
    this.hint = hint;
  }

  public String getHint()
  {
    return hint;
  }

  public void setAllowWikiSyntax(boolean allowWikiSyntax)
  {
    this.allowWikiSyntax = allowWikiSyntax;
  }

  public boolean isAllowWikiSyntax()
  {
    return allowWikiSyntax;
  }

  public void setMaxWidth(String maxWidth)
  {
    this.maxWidth = maxWidth;
  }

  public String getMaxWidth()
  {
    return maxWidth;
  }

  public void setMaxFileSize(String maxFileSize)
  {
    this.maxFileSize = maxFileSize;
  }

  public String getMaxFileSize()
  {
    return maxFileSize;
  }
}

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
package de.micromata.genome.gwiki.pagetemplates_1_0.editor;

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.input;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.table;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiCollectFragmentTypeVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParser;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * @author Christian Claus (c.claus@micromata.de)
 */
public class PtWikiImageEditor extends PtWikiUploadEditor
{

  private static final long serialVersionUID = 5901053792188232570L;

  private String imageConfig = "";

  private String maxWidth;

  public PtWikiImageEditor(GWikiElement element, String sectionName, String editor, String hint, String maxWidth, String maxFileSize)
  {
    super(element, sectionName, editor, hint, maxFileSize);
    this.maxWidth = maxWidth;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(final GWikiContext ctx)
  {
    parseImageConfig(ctx, true);

    final String browse = ctx.getTranslated("gwiki.editor.image.browse");

    XmlElement input = input( //
    attrs("name", sectionName, "type", "file", "size", "50", "accept", "image/*"));

    XmlElement table = table( //
        attrs("style", "margin:20px 0")).nest( //
        tr( //
        td(text(browse)), //
        td(input) //
        ));

    ctx.append(table.toString());
    return true;
  }

  /**
   * @param ctx
   */
  private void parseImageConfig(final GWikiContext ctx, boolean render)
  {
    String content = StringEscapeUtils.escapeHtml(getEditContent());
    GWikiWikiParser wkparse = new GWikiWikiParser();
    GWikiContent gwikiContent = wkparse.parse(ctx, content);

    GWikiCollectFragmentTypeVisitor images = new GWikiCollectFragmentTypeVisitor(GWikiFragmentImage.class);
    gwikiContent.iterate(images);

    GWikiFragmentImage img = null;

    if (!images.getFound().isEmpty()) {
      img = (GWikiFragmentImage) images.getFound().get(0);

      final StringBuilder sb = new StringBuilder();
      img.getSource(sb);
      int idx = sb.indexOf("|");
      if (idx > 1) {
        imageConfig = sb.toString().substring(idx, sb.length() - 1);
      }

      if (render) {
        img.setStyle("margin-top:20px");
        img.render(ctx);
      }
    }
  }

  public void onSave(final GWikiContext ctx)
  {
    parseImageConfig(ctx, false);

    String cleaned = maxWidth.replaceAll("px", "");
    cleaned = StringUtils.trimToEmpty(cleaned);
    String content = null;

    dataFile = ctx.getFileItem(sectionName);

    if (cleaned.length() > 0) {
      try {
        int maxWidthInPx = Integer.parseInt(cleaned);

        /*
         * On some GIF files, the ImageIO.read operation runs into an IOException while reading an unexpected block type. This bug is
         * already known since 2004 and will be fixed up in JDK 7
         * 
         * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6358674
         */
        BufferedImage img = ImageIO.read(dataFile.getInputStream());
        int width = img.getWidth();

        if (width > maxWidthInPx) {
          ctx.addValidationError("gwiki.edit.EditPage.attach.message.uploadfailed", ctx.getTranslated("gwiki.editor.image.tooBig")
              + maxWidthInPx
              + " px");
          return;
        }
      } catch (IOException ex) {
        ctx.addSimpleValidationError(ctx.getTranslated(("gwiki.editor.error")));
        GWikiLog.error("I/O Error: " + ex.getMessage(), ex);
      } catch (NumberFormatException ex) {
        ctx.addSimpleValidationError(ctx.getTranslated(("gwiki.editor.image.numberFormat")));
      }

    }

    if (ctx.hasValidationErrors()) {
      return;
    }

    content = super.saveContent(ctx);

    if (ctx.hasValidationErrors()) {
      return;
    } else {
      String target = content + imageConfig;
      GWikiFragmentImage image = new GWikiFragmentImage(target);
      String newContent = image.toString();

      updateSection(ctx, newContent);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.pagetemplates_1_0.editor.GWikiSectionEditorArtefakt#onDelete(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onDelete(GWikiContext ctx)
  {
    // TODO Auto-generated method stub

  }

}

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
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.p;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * @author Christian Claus (c.claus@micromata.de)
 */
public class PtWikiEditAttachmentEditor extends PtWikiTextEditorBase
{

  private static final long serialVersionUID = 5901053792188232570L;

  private String fieldNumber;

  public PtWikiEditAttachmentEditor(GWikiElement element, String sectionName, String editor, String hint, String fieldNumber)
  {
    super(element, sectionName, editor, hint);

    this.fieldNumber = fieldNumber;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(final GWikiContext ctx)
  {
    String titleValue = "";

    XmlElement intro = p(attrs()).nest(text("Geben Sie einen neuen Titel ein:"));
    ctx.append(intro);

    String[] contentArray = getEditContent().split(",");

    try {
      GWikiFragmentLink link = getLinkForField(ctx, Integer.parseInt(fieldNumber), contentArray);
      titleValue = link.getTitle();
    } catch (NumberFormatException nfe) {
      GWikiLog.warn("failed to parse number", nfe);
      return false;
    }

    XmlElement inputTitle = input( //
    attrs("name", "title", "value", titleValue, "size", "80"));

    ctx.append(inputTitle.toString());

    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#onSave(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onSave(final GWikiContext ctx)
  {
    String title = ctx.getRequest().getParameter("title");

    /*
     * if (!StringUtils.equals(title, GWikiContext.getPageIdFromTitle(title))) {
     * ctx.addSimpleValidationError(ctx.getTranslated("gwiki.editor.upload.allowdSymbols")); return; }
     */
    String[] contentArray = getEditContent().split(",");

    try {
      GWikiFragmentLink link = getLinkForField(ctx, Integer.parseInt(fieldNumber), contentArray);

      if (StringUtils.isEmpty(title)) {
        link.setTitle(GWikiContext.getPageIdFromTitle(link.getTargetPageId()));
      } else {
        link.setTitle(title);
      }

      if (!GWikiFragmentLink.isGlobalUrl(link.getTarget())) {
        final GWikiElement element = ctx.getWikiWeb().findElement(link.getTargetPageId());
        final GWikiElementInfo elementInfo = element.getElementInfo();

        elementInfo.getProps().setStringValue(GWikiPropKeys.TITLE, title);

        ctx.getWikiWeb().saveElement(ctx, element, false);
      }

      updateSection(ctx, link.toString(), fieldNumber);

    } catch (NumberFormatException nfe) {
      GWikiLog.warn("failed to parse number", nfe);
      return;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.pagetemplates_1_0.editor.GWikiSectionEditorArtefakt#onDelete(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onDelete(final GWikiContext ctx)
  {

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#getTabTitle()
   */
  public String getTabTitle()
  {
    // TODO Auto-generated method stub
    return null;
  }

}

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
package de.micromata.genome.gwiki.pagetemplates_1_0.editor;

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.input;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.p;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.table;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiActionBeanArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBean;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanUtils;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * @author Christian Claus (c.claus@micromata.de)
 */
public class PtWikiAttachmentEditor extends PtWikiUploadEditor
{

  private static final long serialVersionUID = 5901053792188232570L;

  private String fieldNumber;

  public PtWikiAttachmentEditor(GWikiElement element, String sectionName, String editor, String hint, String maxFileSize, String fieldNumber)
  {
    super(element, sectionName, editor, hint, maxFileSize);

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
    final String browse = ctx.getTranslated("gwiki.editor.image.browse");

    XmlElement table = table(attrs());
    String titleValue = "";

    XmlElement intro = p(attrs()).nest(text("Editieren Sie bestehende Anhänge oder laden Sie eine neue Datei hoch."));
    ctx.append(intro);

    XmlElement displayTable = generateDisplayTable(ctx);
    ctx.append(displayTable.toString());

    XmlElement inputFile = input( //
    attrs("name", sectionName, "type", "file", "size", "30", "accept", "*"));

    table.nest( //
        tr( //
        td(text(browse)), //
        td(inputFile) //
        )); //

    XmlElement inputTitle = input( //
    attrs("name", "title", "value", titleValue));

    table.nest(tr( //
        td(text("Title: ")), //
        td(inputTitle) //
        ));

    ctx.append(table.toString());

    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#onSave(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onSave(final GWikiContext ctx)
  {
    String href = super.saveContent(ctx);

    if (href == null) {
      return;
    }

    String title = ctx.getRequest().getParameter("title");

    GWikiFragmentLink link = new GWikiFragmentLink(href);
    link.setTarget(href);

    if (StringUtils.isNotEmpty(title)) {
      link.setTitle(title);
    } else {
      link.setTitle(GWikiContext.getPageIdFromTitle(href));
    }

    updateSection(ctx, link.toString(), fieldNumber);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.pagetemplates_1_0.editor.GWikiSectionEditorArtefakt#onDelete(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onDelete(final GWikiContext ctx)
  {
    String deletePageId = updateSection(ctx, StringUtils.EMPTY, fieldNumber);
    GWikiElement deleteElement = ctx.getWikiWeb().getElement(deletePageId);

    runInActionContext(ctx, deleteElement, new Callable<RuntimeException, Void>() {
      public Void call(ActionBean bean) throws RuntimeException
      {
        ActionBeanUtils.dispatchToMethodImpl(bean, "onDelete", ctx);
        return null;
      }
    });

  }

  public Void runInActionContext(final GWikiContext wikiContext, final GWikiElement elementToEdit,
      final Callable<RuntimeException, Void> callback)
  {
    return wikiContext.runElement(elementToEdit, new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        GWikiElement page = wikiContext.getWikiWeb().getElement("edit/EditPage");
        GWikiArtefakt< ? > controller = page.getPart("Controler");
        if (controller instanceof GWikiActionBeanArtefakt == false) {
          return null;
        }
        GWikiActionBeanArtefakt actionBeanArtefakt = (GWikiActionBeanArtefakt) controller;
        ActionBean bean = actionBeanArtefakt.getActionBean(wikiContext);
        bean.setWikiContext(wikiContext);

        Map<String, Object> elementParam = new HashMap<String, Object>();
        elementParam.put("elementToEdit", elementToEdit);
        elementParam.put("pageId", elementToEdit.getElementInfo().getId());
        elementParam.put("parentPageId", elementToEdit.getElementInfo().getParentId());
        ClassUtils.populateBeanWithPuplicMembers(bean, elementParam);

        return callback.call(bean);
      }
    });
  }

  interface Callable<E extends RuntimeException, R>
  {
    R call(ActionBean bean) throws E;
  }

}

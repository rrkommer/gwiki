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

import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiBinaryAttachmentArtefakt;

/**
 * @author Christian Claus (c.claus@micromata.de)
 */
public abstract class PtWikiUploadEditor extends PtWikiTextEditorBase
{
  private static final long serialVersionUID = 5901053792188232570L;

  protected FileItem dataFile;

  private String maxFileSize;

  private byte[] data;

  public PtWikiUploadEditor(GWikiElement element, String sectionName, String editor, String hint, String maxFileSize)
  {
    super(element, sectionName, editor, hint);

    this.maxFileSize = maxFileSize;
  }

  /**
   * @param ctx
   * @return
   */
  public String saveContent(final GWikiContext ctx)
  {
    String parentPageId = uploadFile(ctx);

    if (parentPageId == null) {
      return null;
    }

    String pageId = parentPageId + "/" + dataFile.getName();

    return pageId;
  }

  /**
   * @param ctx
   * @param maxWidthInPx
   * @return
   */
  private String uploadFile(final GWikiContext ctx)
  {
    String parentPageId = ctx.getRequestParameter("pageId");

    if (dataFile == null) {
      dataFile = ctx.getFileItem(sectionName);
    }

    String pageId = parentPageId + "/" + dataFile.getName();
    String title = ctx.getRequest().getParameter("title");
    try {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      IOUtils.copy(dataFile.getInputStream(), bout);
      data = bout.toByteArray();

      String metaTemplateId = "admin/templates/FileWikiPageMetaTemplate";
      GWikiElement imageElement = GWikiWebUtils.createNewElement(ctx, pageId, metaTemplateId, parentPageId);
      GWikiArtefakt< ? > art = imageElement.getMainPart();
      GWikiBinaryAttachmentArtefakt att = (GWikiBinaryAttachmentArtefakt) art;
      att.setStorageData(data);

      if (data != null) {
        imageElement.getElementInfo().getProps().setIntValue(GWikiPropKeys.SIZE, data.length);
      }

      if (!StringUtils.isEmpty(title)) {
        imageElement.getElementInfo().getProps().setStringValue(GWikiPropKeys.TITLE, title);
      } else {
        imageElement.getElementInfo().getProps().setStringValue(GWikiPropKeys.TITLE, dataFile.getName());
      }

      imageElement.getElementInfo().getProps().setStringValue(GWikiPropKeys.PARENTPAGE, parentPageId);

      if (maxFileSize != null && StringUtils.isNotEmpty(maxFileSize)) {
        try {
          long maxSize = Long.parseLong(StringUtils.trimToEmpty(maxFileSize)) * 1024;
          long currSize = dataFile.getSize();

          if (maxSize < currSize) {
            ctx.addSimpleValidationError(ctx.getTranslated("gwiki.editor.upload.filesize") + maxFileSize + " kB");
          } else {
            ctx.getWikiWeb().saveElement(ctx, imageElement, false);
          }
        } catch (Exception e) {
          GWikiLog.error(e.getMessage(), e);
        }
      } else {
        ctx.getWikiWeb().saveElement(ctx, imageElement, false);
      }
    } catch (IOException ex) {
      ctx.addValidationError("gwiki.edit.EditPage.attach.message.uploadfailed", ex.getMessage());
    }

    return parentPageId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#getTabTitle()
   */
  public String getTabTitle()
  {
    return "";
  }

}
